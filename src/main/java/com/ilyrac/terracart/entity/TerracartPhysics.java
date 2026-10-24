package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TerracartPhysics {
    public static Vec3 applyGravity(TerracartEntity cart, Vec3 motion) {
        if (!cart.onGround()) {
            double newY = Math.max(motion.y - 0.08, -1.0);
            return new Vec3(motion.x, newY, motion.z);
        }
        return motion.y < 0.0 ? new Vec3(motion.x, 0.0, motion.z) : motion;
    }

    public static Vec3 applyControllerInput(TerracartEntity cart, Vec3 motion) {
        // Top speed is mapped directly from the core's BPS stat (17 to 25 blocks per second)
        double topSpeedBps = cart.getCoreStats().maxSpeed();
        double maxForwardSpeed = topSpeedBps / 20.0;
        final double MAX_REVERSE_SPEED = cart.getTier().getReverseSpeed() / 20.0; // Tier-specific: Swift=8, Prime=7, Standard=6, Sturdy=5
        final double ACCEL = 0.005, FRICTION = 0.97, BRAKE_FACTOR = 6.0;
        final int SHIFT_DELAY = 6; // 6 ticks = 0.3s

        boolean inLeaves = cart.isInLeaves();
        double waterHeight = cart.getFluidHeight(FluidTags.WATER);

        // 1. Lava Hazard: viscous molten stall and sinking
        if (cart.isInLava()) {
            cart.setBrakingActive(false);
            cart.setCurrentSpeed(0.0);
            Vec3 horiz = new Vec3(motion.x, 0.0, motion.z).scale(0.85);
            return new Vec3(horiz.x, Math.max(motion.y, -0.25) * 0.85, horiz.z);
        }

        // 2. Deep Water: Engine floods and stalls (Option A: Sinking Anchor in 2+ block water)
        if (waterHeight > 1.05) {
            cart.setBrakingActive(false);
            if (Math.abs(cart.getCurrentSpeed()) > 0.001) {
                if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                    sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(),
                            ModSounds.TERRACART_ENGINE_SPUTTERS, SoundSource.NEUTRAL, 1.0F, 0.9F);
                    TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.BUBBLE, 24, 0.05);
                }
            }
            cart.setCurrentSpeed(0.0);
            Vec3 horiz = new Vec3(motion.x, 0.0, motion.z).scale(0.85);
            return new Vec3(horiz.x, Math.max(motion.y, -0.35) * 0.90, horiz.z);
        }

        // 3. Shallow Water (Wading / Fording): 75% Speed & Acceleration Drag (25% effective)
        boolean inShallowWater = waterHeight > 0.0;
        double effectiveMaxForwardSpeed = maxForwardSpeed;
        if (inLeaves) effectiveMaxForwardSpeed *= 0.70;
        if (inShallowWater) effectiveMaxForwardSpeed *= 0.25;

        double effectiveMaxReverseSpeed = MAX_REVERSE_SPEED;
        if (inLeaves) effectiveMaxReverseSpeed *= 0.70;
        if (inShallowWater) effectiveMaxReverseSpeed *= 0.25;
        double currentSpeed = cart.getCurrentSpeed();

        // 1. Check if a player is riding
        if (cart.getControllingPassenger() instanceof Player player) {

            // If vehicle is in post-crash recovery stun, freeze throttle input and decay rebound motion smoothly
            if (cart.getStunTicks() > 0) {
                cart.setBrakingActive(false);
                cart.setCurrentSpeed(0.0);
                return new Vec3(motion.x * 0.95, motion.y, motion.z * 0.95);
            }

            // PHYSICS FIX: Only read raw keyboard input if we are the true, live local driver.
            // If we are a Flashback dummy observer, read the Synced network data instead!
            boolean isLiveDriver = cart.level().isClientSide() && player.isLocalPlayer();
            float forward = isLiveDriver ? player.zza : cart.getSyncedForward();
            float strafe = isLiveDriver ? -player.xxa : cart.getSyncedStrafe();

            boolean isBraking = false;

            // 2. Symmetrical Driving, Braking, and Directional Shifting with Gear Memory
            if (cart.hasFuel()) {
                if (currentSpeed > 0.001) {
                    // --- FORWARD MOTION (In Drive Gear) ---
                    cart.setCurrentGear(1);
                    if (forward > 0) {
                        // Accelerating forward
                        cart.setShiftDelayTimer(0);
                        cart.setShiftTargetDirection(0);

                        double effectiveAccel = ACCEL;
                        if (cart.getDescentTimer() > 0) {
                            effectiveAccel = ACCEL * 1.15; // Downhill subtle gravity pickup (+15%)
                        } else if (cart.getInclineTimer() > 0) {
                            // Uphill engine struggle: cart fights hard, especially at low speed
                            if (currentSpeed < 8.0 / 20.0) {
                                effectiveAccel = ACCEL * 0.45; // Heavy struggle at low speeds
                            } else {
                                effectiveAccel = ACCEL * 0.65; // High speed incline resistance
                            }
                        }

                        // Resistance acceleration pushing through leaves or shallow water
                        if (inLeaves) {
                            effectiveAccel *= 0.55;
                        }
                        if (inShallowWater) {
                            effectiveAccel *= 0.25;
                        }

                        currentSpeed += effectiveAccel;

                        // Drain excess speed down when entering foliage or shallow water, then sustain at effectiveMaxForwardSpeed
                        if ((inLeaves || inShallowWater) && currentSpeed > effectiveMaxForwardSpeed) {
                            currentSpeed = Math.max(effectiveMaxForwardSpeed, currentSpeed * 0.94);
                        } else {
                            currentSpeed = Math.min(currentSpeed, effectiveMaxForwardSpeed);
                        }
                    } else if (forward < 0) {
                        // BRAKING from forward motion
                        isBraking = currentSpeed > 0.05;
                        currentSpeed -= ACCEL * BRAKE_FACTOR;
                        if (currentSpeed <= 0.001) {
                            currentSpeed = 0.0;
                            isBraking = false;
                            // Enter shift delay into reverse
                            cart.setShiftDelayTimer(SHIFT_DELAY);
                            cart.setShiftTargetDirection(-1);
                        }
                    } else {
                        // Coasting forward (forward == 0)
                        cart.setShiftDelayTimer(0);
                        cart.setShiftTargetDirection(0);

                        if (cart.getDescentTimer() > 0) {
                            // Downhill: no friction / resistance, auto-accelerates forward!
                            currentSpeed = Math.min(currentSpeed + ACCEL * 0.35, effectiveMaxForwardSpeed);
                        } else if (cart.getInclineTimer() > 0) {
                            // Uphill: rolling resistance (extra friction/drag uphill)
                            currentSpeed *= 0.94;
                            if (currentSpeed < 0.001) currentSpeed = 0.0;
                        } else {
                            currentSpeed *= FRICTION;
                            if ((inLeaves || inShallowWater) && currentSpeed > effectiveMaxForwardSpeed) {
                                currentSpeed = Math.max(effectiveMaxForwardSpeed, currentSpeed * 0.94);
                            }
                            if (currentSpeed < 0.001) currentSpeed = 0.0;
                        }
                    }
                } else if (currentSpeed < -0.001) {
                    // --- REVERSE MOTION (In Reverse Gear) ---
                    cart.setCurrentGear(-1);
                    if (forward < 0) {
                        // Accelerating in reverse
                        cart.setShiftDelayTimer(0);
                        cart.setShiftTargetDirection(0);
                        double effectiveAccel = ACCEL;
                        if (inLeaves) effectiveAccel *= 0.55;
                        if (inShallowWater) effectiveAccel *= 0.25;
                        currentSpeed -= effectiveAccel;
                        currentSpeed = Math.max(currentSpeed, -effectiveMaxReverseSpeed);
                    } else if (forward > 0) {
                        // BRAKING from reverse motion
                        isBraking = currentSpeed < -0.05;
                        currentSpeed += ACCEL * BRAKE_FACTOR;
                        if (currentSpeed >= -0.001) {
                            currentSpeed = 0.0;
                            isBraking = false;
                            // Enter shift delay into forward
                            cart.setShiftDelayTimer(SHIFT_DELAY);
                            cart.setShiftTargetDirection(1);
                        }
                    } else {
                        // Coasting in reverse
                        cart.setShiftDelayTimer(0);
                        cart.setShiftTargetDirection(0);
                        currentSpeed *= FRICTION;
                        if ((inLeaves || inShallowWater) && currentSpeed < -effectiveMaxReverseSpeed) {
                            currentSpeed = Math.min(-effectiveMaxReverseSpeed, currentSpeed * 0.94);
                        }
                        if (currentSpeed > -0.001) currentSpeed = 0.0;
                    }
                } else {
                    // --- AT COMPLETE STOP (currentSpeed == 0.0) ---
                    currentSpeed = 0.0;
                    int timer = cart.getShiftDelayTimer();
                    int targetDir = cart.getShiftTargetDirection();
                    int gear = cart.getCurrentGear();

                    if (timer > 0) {
                        // Currently in the 6-tick directional pause
                        boolean holdingTarget = (targetDir == -1 && forward < 0) || (targetDir == 1 && forward > 0);
                        if (!holdingTarget) {
                            // Player released input during pause (panic-stop or cancelled maneuver)
                            cart.setShiftDelayTimer(0);
                            cart.setShiftTargetDirection(0);
                        } else {
                            timer--;
                            cart.setShiftDelayTimer(timer);
                            if (timer == 0) {
                                // Transition complete! Trigger shift audio on server
                                if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                                    sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(),
                                            ModSounds.TERRACART_SHIFT, SoundSource.NEUTRAL, 0.75F, 1.0F);
                                }
                                cart.setCurrentGear(targetDir);
                                cart.setShiftTargetDirection(0);
                                // Engage initial momentum in target direction
                                currentSpeed = targetDir * ACCEL;
                            }
                        }
                    } else {
                        // Stationary at stop (timer == 0) - respect transmission gear!
                        if (gear == 1) {
                            // Currently in Drive (Forward)
                            if (forward > 0) {
                                double effectiveAccel = (cart.getInclineTimer() > 0) ? ACCEL * 0.45 : (cart.getDescentTimer() > 0 ? ACCEL * 1.15 : ACCEL);
                                if (inLeaves) effectiveAccel *= 0.55;
                                if (inShallowWater) effectiveAccel *= 0.25;
                                currentSpeed += effectiveAccel;
                                currentSpeed = Math.min(currentSpeed, effectiveMaxForwardSpeed);
                            } else if (forward < 0) {
                                // Must shift into Reverse!
                                cart.setShiftDelayTimer(SHIFT_DELAY);
                                cart.setShiftTargetDirection(-1);
                            } else if (cart.getDescentTimer() > 0) {
                                // Downhill slope without player forward button: auto-roll begins!
                                currentSpeed = ACCEL * 0.25;
                            }
                        } else {
                            // Currently in Reverse
                            if (forward < 0) {
                                double effectiveAccel = ACCEL;
                                if (inLeaves) effectiveAccel *= 0.55;
                                if (inShallowWater) effectiveAccel *= 0.25;
                                currentSpeed -= effectiveAccel;
                                currentSpeed = Math.max(currentSpeed, -effectiveMaxReverseSpeed);
                            } else if (forward > 0) {
                                // Must shift into Drive!
                                cart.setShiftDelayTimer(SHIFT_DELAY);
                                cart.setShiftTargetDirection(1);
                            }
                        }
                    }
                }
            } else {
                // Out of fuel: ignore throttle, cancel shift, and decelerate smoothly
                cart.setShiftDelayTimer(0);
                cart.setShiftTargetDirection(0);

                if (cart.getDescentTimer() > 0 && currentSpeed > 0) {
                    // Out of fuel, but rolls down the hill with gravity!
                    currentSpeed = Math.min(currentSpeed + ACCEL * 0.3, maxForwardSpeed);
                } else {
                    currentSpeed *= FRICTION;
                    if (Math.abs(currentSpeed) < 0.001) currentSpeed = 0.0;
                }
            }

            // Foliage rustle sound when driving through leaves
            if (inLeaves && Math.abs(currentSpeed) > 0.04) {
                if (cart.getLeafSoundCooldown() <= 0) {
                    cart.level().playSound(null, cart.getX(), cart.getY(), cart.getZ(),
                            SoundEvents.CHERRY_LEAVES_STEP, SoundSource.NEUTRAL,
                            0.45F, 0.9F + (cart.getRandom().nextFloat() - 0.5F) * 0.2F);
                    cart.setLeafSoundCooldown(6);
                }
            }

            // Shallow water splash sound when driving through water
            if (inShallowWater && Math.abs(currentSpeed) > 0.03) {
                if (cart.tickCount % 6 == 0) {
                    cart.level().playSound(null, cart.getX(), cart.getY(), cart.getZ(),
                            SoundEvents.GENERIC_SPLASH, SoundSource.NEUTRAL,
                            0.35F, 1.1F + (cart.getRandom().nextFloat() - 0.5F) * 0.2F);
                }
            }

            cart.setCurrentSpeed(currentSpeed);
            cart.setBrakingActive(isBraking);

            // 3. Turning Logic: Allow steering while coasting until the cart comes to a complete halt
            if (Math.abs(currentSpeed) > 0.01 && strafe != 0) {
                float turnDirection = (float) Math.signum(currentSpeed);
                float turn = 7.5F * strafe * turnDirection * (0.3F + 0.7F * (1.0F - Math.min((float) (Math.abs(currentSpeed) / maxForwardSpeed), 1.0F)));
                cart.setYRot(cart.getYRot() + turn);
            }

            // 4. Continue applying directional momentum using currentSpeed while coasting
            float rad = (float) Math.toRadians(cart.getYRot());
            if (Math.abs(currentSpeed) < 0.001) {
                // Throttle is at zero (coasting from external impulse, bumper bounce, or idling):
                // Preserve physical momentum with standard rolling friction exactly like an empty cart!
                return new Vec3(motion.x * 0.95, motion.y, motion.z * 0.95);
            }
            return new Vec3(-Math.sin(rad) * currentSpeed, motion.y, Math.cos(rad) * currentSpeed);
        }

        // No driver riding: apply friction, cancel shift, reset gear to Drive, and coast to halt
        cart.setBrakingActive(false);
        cart.setCurrentGear(1);
        cart.setShiftDelayTimer(0);
        cart.setShiftTargetDirection(0);
        currentSpeed *= FRICTION;
        if (inLeaves && currentSpeed > effectiveMaxForwardSpeed) {
            currentSpeed = Math.max(effectiveMaxForwardSpeed, currentSpeed * 0.96);
        }
        if (Math.abs(currentSpeed) < 0.001) currentSpeed = 0.0;
        cart.setCurrentSpeed(currentSpeed);

        return new Vec3(motion.x * 0.95, motion.y, motion.z * 0.95);
    }

    public static boolean handleStepUp(TerracartEntity cart, Vec3 origMotion) {
        if (!cart.horizontalCollision) return false;

        double horizDist = new Vec3(origMotion.x, 0.0, origMotion.z).horizontalDistance();
        double dirX = horizDist > 1.0E-5 ? origMotion.x / horizDist : 0.0;
        double dirZ = horizDist > 1.0E-5 ? origMotion.z / horizDist : 0.0;

        // Clamped step check distance:
        // When driving at high speeds (e.g. Swift cart at 22 BPS = 1.1 blocks/tick),
        // projecting the full motion vector into a continuous staircase hill overshoots Step 1
        // and collides with Step 2, failing step-up on the first tick and causing a hitch/blink.
        // Checking clearance at immediate step landing (~0.25-0.35 blocks) allows the cart to mount
        // Step 1 cleanly, while remaining momentum carries it onto the step.
        double stepCheckDist = Math.clamp(horizDist, 0.25, 0.35);
        double stepCheckX = dirX * stepCheckDist;
        double stepCheckZ = dirZ * stepCheckDist;

        boolean isBlocked = cart.level().getBlockCollisions(cart, cart.getBoundingBox().move(origMotion.x, 0.0, origMotion.z)).iterator().hasNext()
                || cart.level().getBlockCollisions(cart, cart.getBoundingBox().move(stepCheckX, 0.0, stepCheckZ)).iterator().hasNext();
        Vec3 stepResult = null;

        boolean nearGround = cart.onGround() || !cart.level().noCollision(cart, cart.getBoundingBox().move(0.0, -0.6, 0.0));
        if (nearGround && origMotion.horizontalDistanceSqr() >= 1.0E-6) {
            AABB box = cart.getBoundingBox();
            for (int step = 1; step <= 5; step++) {
                double y = step * 0.2;
                if (cart.level().noCollision(cart, box.move(0, y, 0)) && isBlocked) {
                    boolean canStepFull = cart.level().noCollision(cart, box.move(origMotion.x, y, origMotion.z));
                    boolean canStepImmediate = cart.level().noCollision(cart, box.move(stepCheckX, y, stepCheckZ));
                    if (canStepFull || canStepImmediate) {
                        stepResult = new Vec3(origMotion.x, y, origMotion.z);
                        break;
                    }
                }
            }
        }

        if (stepResult != null) {
            cart.setPos(cart.xo, cart.yo, cart.zo);
            cart.move(MoverType.SELF, stepResult);
            if (cart.onGround()) {
                cart.setDeltaMovement(cart.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            } else {
                cart.setDeltaMovement(cart.getDeltaMovement().x, Math.min(cart.getDeltaMovement().y, 0.0), cart.getDeltaMovement().z);
            }

            // Uphill step resistance & kinetic momentum carry (paced by 0.2s cooldown window)
            if (cart.getStepBleedCooldown() == 0) {
                double current = cart.getCurrentSpeed();
                double minClimbSpeed = 6.0 / 20.0; // 6 BPS guaranteed anti-stall climb speed
                if (current > minClimbSpeed) {
                    cart.setCurrentSpeed(Math.max(minClimbSpeed, current * 0.90)); // 10% speed bleed per window
                }
                cart.setStepBleedCooldown(4); // 4 ticks = 0.2s cooldown
            }
            cart.setInclineTimer(4);
            return true;
        } else {
            // Cannot step up: gentle speed dampen on obstacle (not a crash, just terrain resistance)
            cart.setCurrentSpeed(cart.getCurrentSpeed() * 0.95);
            return false;
        }
    }

    public static void handleStepDown(TerracartEntity cart, Vec3 origMotion) {
        // STRICT LOCKOUT: Only block step-down if the cart is actively ascending right now.
        // This prevents the oscillation loop on stairs while allowing the cart to step down
        // on the other side of a single block without getting stuck.
        if (cart.getInclineTimer() > 0 && cart.getY() >= cart.yo) {
            return;
        }

        // Momentum Gate: At normal/high driving speeds, do not snap down; let the cart carry momentum over drops!
        double horizSpeed = new Vec3(origMotion.x, 0.0, origMotion.z).horizontalDistance();
        if (horizSpeed * 20.0 > 7.0) {
            return;
        }

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
                // Engage downhill timer (enables zero friction & auto-roll without artificial speed injection)
                if (cart.getCurrentSpeed() > 0) {
                    cart.setDescentTimer(12);
                }
            }
        }
    }

    public static void handleLandFallDamage(TerracartEntity cart) {
        if (!cart.onGround()) {
            if (cart.isInWater()) {
                cart.setWasAirborne(false);
                return;
            }
            if (!cart.wasAirborne()) {
                cart.setWasAirborne(true);
                cart.setAirborneStartY(cart.getY());
            } else if (cart.getY() > cart.getAirborneStartY()) {
                // If cart was moving upward (jump or bump apex), track the highest point
                cart.setAirborneStartY(cart.getY());
            }
            return;
        }

        // Cart is on ground: check if we just landed from being airborne
        if (cart.wasAirborne()) {
            double fallDist = cart.getAirborneStartY() - cart.getY();
            cart.setWasAirborne(false);

            // 0 - 3m drop: Safe ramp / slope landing (0 damage, 0 fuel loss, 0 stun)
            if (fallDist > 3.0 && fallDist <= 5.0) {
                // Soft bump landing (3 - 5m): 1.0 - 2.0 HP chassis scrape, zero fuel leak
                if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                    float bumpDamage = 1.0F + cart.getRandom().nextFloat(); // 1.0 to 2.0 HP
                    cart.setHealth(Math.max(0.0F, cart.getHealth() - bumpDamage));
                    sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 0.8F, 1.05F);
                    TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, 16, 0.05);
                    if (cart.getHealth() <= 0.0F) {
                        TerracartStateManager.executeDestruction(cart, sl);
                    }
                    cart.needsSync = true;
                }
            } else if (fallDist > 5.0) {
                // Hard crash landing (> 5m): 5.0 - 15.0 HP, 1 - 5 FU fuel leak, full stun
                double crashProgress = Mth.clamp((fallDist - 5.0) / 10.0, 0.0, 1.0);
                float cartDamage = (float) Mth.lerp(crashProgress, 5.0, 15.0);
                boolean isSevere = fallDist > 9.0;
                float playerDamage = isSevere ? 6.0f : 3.0f; // 3 hearts (6 HP) if severe, 1.5 hearts (3 HP) if medium
                int fuelLossUnits = 1 + cart.getRandom().nextInt(5); // 1 to 5 FU leaked!

                TerracartCollisionHandler.applyImpactConsequences(cart, cartDamage, playerDamage, fuelLossUnits, isSevere, false, 0.0, 0.0);
            }
        }
    }
}