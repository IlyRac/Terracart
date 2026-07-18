package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TerracartPhysics {

    public static Vec3 applyGravity(TerracartEntity cart, Vec3 motion) {
        if (!cart.onGround()) {
            motion = motion.add(0.0, -1.0, 0.0);
            return motion.y < -1.0 ? new Vec3(motion.x, -1.0, motion.z) : motion;
        }
        return motion.y < 0.0 ? new Vec3(motion.x, 0.0, motion.z) : motion;
    }

    public static Vec3 applyControllerInput(TerracartEntity cart, Vec3 motion) {
        final double MAX_SPEED = 1.0, ACCEL = 0.005, FRICTION = 0.97;

        if (cart.isInWater() || cart.isInLava()) {
            double factor = cart.isInLava() ? 0.12 : 0.06;
            cart.setCurrentSpeed(Mth.lerp(factor, cart.getCurrentSpeed(), 0.0));
            Vec3 horiz = new Vec3(motion.x, 0.0, motion.z).scale(Math.max(0.0, 1.0 - factor));
            return new Vec3(horiz.x, Math.max(motion.y, -0.6) * 0.85, horiz.z);
        }

        double currentSpeed = cart.getCurrentSpeed();

        // 1. Check if a player is riding
        if (cart.getControllingPassenger() instanceof Player player) {
            float forward = cart.level().isClientSide() ? player.zza : cart.getDriverForward();
            float strafe = cart.level().isClientSide() ? -player.xxa : cart.getDriverStrafe();

            // 2. Only allow acceleration if the tank has fuel!
            if (cart.hasFuel()) {
                if (forward > 0) currentSpeed += (currentSpeed < 0 ? ACCEL * 3 : ACCEL);
                else if (forward < 0) currentSpeed -= (currentSpeed > 0 ? ACCEL * 6 : ACCEL);
                else currentSpeed *= FRICTION;
            } else {
                // Out of fuel: ignore player forward/backward throttle and decelerate smoothly
                currentSpeed *= FRICTION;
            }

            currentSpeed = Mth.clamp(currentSpeed, -MAX_SPEED, MAX_SPEED);
            if (Math.abs(currentSpeed) < 0.001) currentSpeed = 0.0;
            cart.setCurrentSpeed(currentSpeed);

            // 3. Turning Logic: Allow steering while coasting until the cart comes to a complete halt
            if (Math.abs(currentSpeed) > 0.01 && strafe != 0) {
                float turn = 7.5F * strafe * (0.3F + 0.7F * (1.0F - Math.min((float) (Math.abs(currentSpeed) / MAX_SPEED), 1.0F)));
                cart.setYRot(cart.getYRot() + turn);
            }

            if (Math.abs(Mth.wrapDegrees(cart.getYRot() - cart.yRotO)) > 0.0001F) {
                player.setYRot(player.getYRot() + Mth.wrapDegrees(cart.getYRot() - cart.yRotO));
                player.yRotO = player.getYRot();
            }

            // 4. Continue applying directional momentum using currentSpeed while coasting
            float rad = (float) Math.toRadians(cart.getYRot());
            return new Vec3(-Math.sin(rad) * currentSpeed, motion.y, Math.cos(rad) * currentSpeed);
        }

        // No driver riding: apply friction and coast to halt
        currentSpeed *= FRICTION;
        if (Math.abs(currentSpeed) < 0.001) currentSpeed = 0.0;
        cart.setCurrentSpeed(currentSpeed);

        return new Vec3(motion.x * 0.95, motion.y, motion.z * 0.95);
    }

    public static void handleStepUp(TerracartEntity cart, Vec3 origMotion) {
        if (!cart.horizontalCollision) return;

        boolean isBlocked = cart.level().getBlockCollisions(cart, cart.getBoundingBox().move(origMotion.x, 0.0, origMotion.z)).iterator().hasNext();
        Vec3 stepResult = null;

        if (cart.onGround() && origMotion.horizontalDistanceSqr() >= 1.0E-6) {
            AABB box = cart.getBoundingBox();
            for (double y = 0.2; y <= 1.0; y += 0.2) {
                if (cart.level().noCollision(cart, box.move(0, y, 0)) && cart.level().noCollision(cart, box.move(origMotion.x, y, origMotion.z)) && isBlocked) {
                    stepResult = new Vec3(origMotion.x, y, origMotion.z);
                    break;
                }
            }
        }

        if (stepResult != null) {
            cart.setPos(cart.xo, cart.yo, cart.zo);
            cart.move(MoverType.SELF, stepResult);
            if (cart.onGround()) cart.setDeltaMovement(cart.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            cart.setCurrentSpeed(cart.getCurrentSpeed() * 0.98);
        } else {
            if (isBlocked) {
                TerracartCollisionHandler.performCrashImpact(cart, origMotion);
                cart.setCurrentSpeed(cart.getCurrentSpeed() * 0.95);
            } else {
                cart.setCurrentSpeed(cart.getCurrentSpeed() * 0.5);
            }
        }
    }

    public static void handleStepDown(TerracartEntity cart, Vec3 origMotion) {
        // If the cart is moving forward, was just on the ground, but is now slightly airborne downward
        if (!cart.onGround() && cart.getDeltaMovement().y <= 0.0 && origMotion.horizontalDistanceSqr() >= 1.0E-6) {
            AABB box = cart.getBoundingBox();
            Vec3 downstreamMotion = new Vec3(origMotion.x, 0.0, origMotion.z);

            // Scan downward up to 1.0 block to see if there is solid ground directly beneath the next step
            boolean foundGroundBelow = false;
            double dropY = 0.0;

            for (double y = 0.2; y <= 1.0; y += 0.2) {
                // If there's no collision at the moved position, but moving a bit further down hits ground
                if (cart.level().noCollision(cart, box.move(downstreamMotion.x, -y, downstreamMotion.z))) {
                    if (!cart.level().noCollision(cart, box.move(downstreamMotion.x, -(y + 0.2), downstreamMotion.z))) {
                        foundGroundBelow = true;
                        dropY = -y;
                        break;
                    }
                }
            }

            if (foundGroundBelow) {
                // Snap the cart down to the step so it doesn't float
                cart.move(MoverType.SELF, new Vec3(0.0, dropY, 0.0));
                // Reset vertical velocity accumulation so it doesn't cause a speed explosion
                Vec3 currentMove = cart.getDeltaMovement();
                cart.setDeltaMovement(currentMove.x, 0.0, currentMove.z);
                // Soften the high-speed descent slightly so it doesn't launch wildly
                cart.setCurrentSpeed(cart.getCurrentSpeed() * 0.99);
            }
        }
    }

    public static void handleLandFallDamage(TerracartEntity cart) {
        if (cart.wasAirborne() && cart.onGround()) {
            double fallDist = cart.getAirborneStartY() - cart.getY();
            if (fallDist > 3.5) {
                cart.setCurrentSpeed(cart.getCurrentSpeed() * (1.0 - Math.min((fallDist - 3.5) * 0.15, 0.9)));
                if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                    float damage = Mth.clamp((float) ((fallDist - 3.5) * 0.5), 1.0f, 6.0f);
                    cart.setHealth(cart.getHealth() - damage);
                    sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), ModSounds.TERRACART_CRASH, SoundSource.NEUTRAL, 0.6F, 1.0F);
                    sl.sendParticles(ParticleTypes.SQUID_INK, cart.getX(), cart.getY() + 1.0, cart.getZ(), 6, 0.12, 0.12, 0.12, 0.08);
                    if (cart.getHealth() <= 0) TerracartStateManager.executeDestruction(cart, sl);
                    cart.setHitCooldown(Math.max(cart.getHitCooldown(), 20));
                }
            }
            cart.setWasAirborne(false);
        } else if (!cart.onGround() && !cart.wasAirborne()) {
            cart.setWasAirborne(true);
            cart.setAirborneStartY(cart.getY());
        }
    }
}