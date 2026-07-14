package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class TerracartCollisionHandler {

    public static void handleCollisions(TerracartEntity cart, double speed) {
        List<Entity> list = cart.level().getEntities(cart, cart.getBoundingBox().inflate(0.2, -0.01, 0.2), EntitySelector.pushableBy(cart));
        for (Entity target : list) {
            if (target.hasPassenger(cart)) continue;
            cart.push(target); // This calls the delegated push override

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