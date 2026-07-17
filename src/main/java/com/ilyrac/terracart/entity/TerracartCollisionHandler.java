package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class TerracartCollisionHandler {

    // Keep the dimensions central here
    public static final double CART_LENGTH = 2.8;
    public static final double CART_WIDTH = 1.4;
    public static final float BOX_HEIGHT = 1.25F;

    /**
     * Checks if a yaw angle corresponds to an East/West alignment.
     */
    public static boolean isYawEastWest(float yaw) {
        float normalized = (yaw % 360.0F + 360.0F) % 360.0F;
        return (normalized >= 45.0F && normalized < 135.0F) || (normalized >= 225.0F && normalized < 315.0F);
    }

    /**
     * Dry-runs a rotation change to see if the new bounding box would collide with solid walls.
     * Returns true if the rotation is safe, false if it should be blocked.
     */
    public static boolean canRotateTo(TerracartEntity cart, float targetYaw) {
        float currentYaw = cart.getYRot();
        if (Mth.equal(currentYaw, targetYaw)) {
            return true;
        }

        boolean currentIsEW = isYawEastWest(currentYaw);
        boolean targetIsEW = isYawEastWest(targetYaw);

        // Only perform the block collision check if the box orientation is actually changing
        if (currentIsEW != targetIsEW) {
            double targetBoxX = targetIsEW ? CART_LENGTH : CART_WIDTH;
            double targetBoxZ = targetIsEW ? CART_WIDTH : CART_LENGTH;

            AABB targetBox = new AABB(
                    cart.getX() - (targetBoxX / 2.0),
                    cart.getY(),
                    cart.getZ() - (targetBoxZ / 2.0),
                    cart.getX() + (targetBoxX / 2.0),
                    cart.getY() + BOX_HEIGHT,
                    cart.getZ() + (targetBoxZ / 2.0)
            );

            // Block rotation if it intersects a solid block
            return !cart.level().collidesWithSuffocatingBlock(cart, targetBox);
        }

        return true;
    }

    /**
     * Generates a bounding box aligned to the current orientation of the cart.
     */
    public static AABB getCalculatedBoundingBox(double x, double y, double z, float yaw) {
        double boxX = isYawEastWest(yaw) ? CART_LENGTH : CART_WIDTH;
        double boxZ = isYawEastWest(yaw) ? CART_WIDTH : CART_LENGTH;

        return new AABB(
                x - (boxX / 2.0),
                y,
                z - (boxZ / 2.0),
                x + (boxX / 2.0),
                y + BOX_HEIGHT,
                z + (boxZ / 2.0)
        );
    }

    public static void handleCollisions(TerracartEntity cart, double speed) {
        List<Entity> list = cart.level().getEntities(cart, cart.getBoundingBox().inflate(0.2, -0.01, 0.2), EntitySelector.pushableBy(cart));
        for (Entity target : list) {
            if (target.hasPassenger(cart)) continue;
            cart.push(target);

            if (cart.getHitCooldown() == 0 && speed > 0.1 && target instanceof LivingEntity living && living != cart.getControllingPassenger()) {
                if (target.position().subtract(cart.position()).lengthSqr() <= 1.0E-6) continue;
                if (!cart.level().isClientSide()) {
                    //noinspection deprecation
                    living.hurt(cart.damageSources().generic(), Mth.clamp((float) (speed * 6.0), 1.5F, 5.0F));
                    Vec3 kb = target.position().subtract(cart.position()).normalize();
                    living.push(kb.x * (0.5 + Math.min(speed, 2.0)), 0.15, kb.z * (0.5 + Math.min(speed, 2.0)));
                }
                cart.setHitCooldown(20);
            }
        }
    }

    // Moved push logic here
    public static void performPush(TerracartEntity cart, Entity entity) {
        if (cart.level().isClientSide() || cart.isRemoved()) return;

        double dx = entity.getX() - cart.getX();
        double dz = entity.getZ() - cart.getZ();
        double dist = Mth.absMax(dx, dz);

        if (dist >= 0.01) {
            dist = Math.sqrt(dist);
            dx /= dist;
            dz /= dist;
            double inverse = Math.min(1.0, 1.0 / dist);
            dx *= inverse * 0.001;
            dz *= inverse * 0.001;

            cart.push(-dx, 0.0, -dz);
            entity.push(dx, 0.0, dz);
        }
    }

    public static void performCrashImpact(TerracartEntity cart, Vec3 motion) {
        if (cart.level().isClientSide() || cart.getHitCooldown() > 0) return;
        double horizSpeed = new Vec3(motion.x, 0.0, motion.z).horizontalDistance();
        if (horizSpeed <= 0.3) return;

        float damage = (float) Mth.clamp(horizSpeed * 6, 1.0f, 6.0f);
        cart.setHealth(cart.getHealth() - damage);

        if (cart.level() instanceof ServerLevel sl) {
            sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), ModSounds.TERRACART_CRASH, SoundSource.NEUTRAL, 0.6F, 1.0F);
            sl.sendParticles(ParticleTypes.SQUID_INK, cart.getX(), cart.getY() + 1.0, cart.getZ(), 6, 0.12, 0.12, 0.12, 0.08);
            if (cart.getHealth() <= 0.0f) TerracartStateManager.executeDestruction(cart, sl);
        }
        cart.setHitCooldown(30);
    }
}