package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

@SuppressWarnings("deprecation")
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

    public enum WeightClass {
        LIGHT,
        MEDIUM,
        HEAVY
    }

    public static WeightClass getWeightClass(LivingEntity entity) {
        if (entity.getType() == EntityTypes.IRON_GOLEM
                || entity.getType() == EntityTypes.RAVAGER
                || entity.getType() == EntityTypes.WARDEN
                || entity.getType() == EntityTypes.WITHER
                || entity.getMaxHealth() >= 80.0F
                || (entity.getBbWidth() >= 1.3F && entity.getBbHeight() >= 1.8F)) {
            return WeightClass.HEAVY;
        }

        if (entity.isBaby()
                || (entity.getBbWidth() <= 0.65F && entity.getBbHeight() <= 0.85F)
                || entity.getMaxHealth() <= 6.0F) {
            return WeightClass.LIGHT;
        }

        return WeightClass.MEDIUM;
    }

    public static void handleCollisions(TerracartEntity cart, double speed) {
        if (cart.getStunTicks() > 0) return;
        List<Entity> list = cart.level().getEntities(cart, cart.getBoundingBox().inflate(0.2, -0.01, 0.2),
                e -> !e.isSpectator() && !cart.hasPassenger(e) && !e.hasPassenger(cart));
        double bps = speed * 20.0;
        float yaw = cart.getYRot();
        double rad = Math.toRadians(yaw);
        Vec3 forward = new Vec3(-Math.sin(rad), 0.0, Math.cos(rad));
        Vec3 lateral = new Vec3(-forward.z, 0.0, forward.x);

        // Travel direction aware of reverse gear
        double forwardSign = cart.getCurrentSpeed() < -0.001 ? -1.0 : 1.0;
        Vec3 travelDir = forward.scale(forwardSign);

        List<LivingEntity> collidingLiving = new java.util.ArrayList<>();
        boolean hasHeavyMob = false;
        boolean hasMediumMob = false;

        for (Entity target : list) {
            if (target.isRemoved()) continue;
            if (target == cart.getControllingPassenger()) continue;
            if (target instanceof TerracartEntity) continue; // Handled in handleCartToCartCollisions
            if (target instanceof ItemEntity || target instanceof ExperienceOrb) continue; // Smooth drive-through pickup

            // 1. END CRYSTALS: Immediate rupture detonation
            if (target instanceof EndCrystal crystal) {
                if (!crystal.isRemoved()) {
                    if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                        crystal.discard();
                        sl.explode(null, crystal.getX(), crystal.getY(), crystal.getZ(), 6.0F, Level.ExplosionInteraction.BLOCK);
                    }
                    return; // Cart is blasted into the air
                }
            }

            // 2. PRIMED TNT: Soccer ball punt
            if (target instanceof PrimedTnt tnt) {
                double kickSpeed = Mth.clamp(speed * 1.8, 0.55, 1.10);
                double lobY = 0.22 + Math.min(speed * 0.2, 0.10);
                tnt.setDeltaMovement(travelDir.x * kickSpeed, lobY, travelDir.z * kickSpeed);
                tnt.needsSync = true;

                if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                    sl.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 0.85F, 1.2F);
                    TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, 12, 0.06);
                }
                continue;
            }

            // 3. ARMOR STANDS: Strongly nudge away with zero damage / zero breaking
            if (target instanceof ArmorStand stand) {
                Vec3 toStand = stand.position().subtract(cart.position());
                double distSq = toStand.x * toStand.x + toStand.z * toStand.z;
                double pushDirX = distSq > 1.0E-6 ? toStand.x / Math.sqrt(distSq) : lateral.x;
                double pushDirZ = distSq > 1.0E-6 ? toStand.z / Math.sqrt(distSq) : lateral.z;

                double shoveStrength = Mth.clamp(0.25 + speed * 0.8, 0.30, 0.65);
                stand.setDeltaMovement(stand.getDeltaMovement().add(pushDirX * shoveStrength, 0.05, pushDirZ * shoveStrength));
                stand.needsSync = true;

                if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                    sl.playSound(null, stand.getX(), stand.getY(), stand.getZ(), ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 0.5F, 1.3F);
                    TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, 6, 0.04);
                }
                continue;
            }

            // 4. OTHER VEHICLES (Minecarts & Boats): Strongly nudge out of the way (no crash or bump)
            if (target instanceof VehicleEntity otherVehicle) {
                Vec3 toVehicle = otherVehicle.position().subtract(cart.position());
                double distSq = toVehicle.x * toVehicle.x + toVehicle.z * toVehicle.z;
                double pushDirX = distSq > 1.0E-6 ? toVehicle.x / Math.sqrt(distSq) : travelDir.x;
                double pushDirZ = distSq > 1.0E-6 ? toVehicle.z / Math.sqrt(distSq) : travelDir.z;

                double shoveSpeed = Mth.clamp(0.25 + speed * 1.0, 0.25, 0.75);
                otherVehicle.setDeltaMovement(otherVehicle.getDeltaMovement().add(pushDirX * shoveSpeed, 0.05, pushDirZ * shoveSpeed));
                otherVehicle.needsSync = true;

                if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                    sl.playSound(null, otherVehicle.getX(), otherVehicle.getY(), otherVehicle.getZ(), ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 0.6F, 1.1F);
                    TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, 10, 0.05);
                }
                continue;
            }

            // 5. LIVING MOBS
            // Push non-cart entities away from cart (without pushing cart backward)
            cart.push(target);

            if (target instanceof LivingEntity living && living != cart.getControllingPassenger()) {
                if (target.position().subtract(cart.position()).lengthSqr() <= 1.0E-6) continue;

                collidingLiving.add(living);
                WeightClass weight = getWeightClass(living);
                if (weight == WeightClass.HEAVY) hasHeavyMob = true;
                if (weight == WeightClass.MEDIUM) hasMediumMob = true;
            }
        }

        if (collidingLiving.isEmpty()) return;

        // --- 1. HEAVY MOBS (Iron Golem, Ravager, Warden, Wither) ---
        if (hasHeavyMob) {
            if (cart.getStunTicks() > 0) return;

            if (bps < 8.0) {
                // Low speed (< 8 BPS): Universal Safe Bump (0 damage, 5-tick stun, 0 whiplash)
                applySafeBump(cart, travelDir.x, travelDir.z);
                return;
            }

            // High speed (>= 8 BPS): True heavy crash impact!
            LivingEntity heavyTarget = null;
            for (LivingEntity l : collidingLiving) {
                if (getWeightClass(l) == WeightClass.HEAVY) {
                    heavyTarget = l;
                    break;
                }
            }
            if (heavyTarget == null) heavyTarget = collidingLiving.get(0);

            boolean isSevere = bps > 16.0;
            float cartDamage = isSevere ? 6.0f + cart.getRandom().nextInt(4) : 3.0f + cart.getRandom().nextInt(4);
            float playerDamage = isSevere ? 6.0f : 3.0f; // 3 hearts (6 HP) if severe, 1.5 hearts (3 HP) if medium
            int fuelLossUnits = 2 + cart.getRandom().nextInt(3); // 2 to 4 FU

            // Attributed ramming damage to heavy mob
            if (!cart.level().isClientSide()) {
                float mobDamage = (float) Mth.lerp(Math.min(1.0, (bps - 8.0) / 14.0), 6.0, 22.0);
                LivingEntity driver = cart.getControllingPassenger();
                DamageSource dmgSrc = (driver instanceof Player player) ? cart.damageSources().playerAttack(player)
                        : (driver != null ? cart.damageSources().mobAttack(driver) : cart.damageSources().generic());
                heavyTarget.hurt(dmgSrc, mobDamage);
            }

            // Universal Critical Crash Consequences (identical to wall crashes)
            applyImpactConsequences(cart, cartDamage, playerDamage, fuelLossUnits, isSevere, true, travelDir.x, travelDir.z);
            return;
        }

        // --- 2. LIGHT & MEDIUM MOBS ---
        if (bps < 8.0) {
            // Maneuvering / Cruising Cow-Catcher (< 8 BPS): Non-damaging wedge shove
            if (!cart.level().isClientSide()) {
                for (LivingEntity living : collidingLiving) {
                    Vec3 toTarget = living.position().subtract(cart.position());
                    double lateralOffset = toTarget.x * lateral.x + toTarget.z * lateral.z;
                    double sideSign = lateralOffset >= 0 ? 1.0 : -1.0;

                    double pushLateral = 0.24 * sideSign;
                    double pushTravel = 0.08 * forwardSign;
                    living.push(forward.x * pushTravel + lateral.x * pushLateral, 0.05, forward.z * pushTravel + lateral.z * pushLateral);
                }
            }
            return;
        }

        // Ramming speed (bps >= 8.0): Full push + damage together for ALL colliding mobs!
        // 1. Vehicle speed bleed (-25% applied once per ramming pass on both client and server)
        if (hasMediumMob) {
            cart.setCurrentSpeed(cart.getCurrentSpeed() * 0.75); // -25% speed bleed
            Vec3 curMove = cart.getDeltaMovement();
            cart.setDeltaMovement(curMove.x * 0.75, curMove.y, curMove.z * 0.75);
        }

        // 2. Damage and fling every colliding mob outward simultaneously
        if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
            float damage = (float) Mth.lerp(Math.min(1.0, (bps - 8.0) / 17.0), 6.0, 22.0);

            LivingEntity driver = cart.getControllingPassenger();
            DamageSource dmgSrc = (driver instanceof Player player) ? cart.damageSources().playerAttack(player)
                    : (driver != null ? cart.damageSources().mobAttack(driver) : cart.damageSources().generic());

            float hitPitch = (float) Mth.clamp(0.9F + (bps / 25.0) * 0.2F, 0.9F, 1.1F);
            sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 0.85F, hitPitch);

            for (LivingEntity living : collidingLiving) {
                WeightClass weight = getWeightClass(living);
                Vec3 toTarget = living.position().subtract(cart.position());
                double lateralOffset = toTarget.x * lateral.x + toTarget.z * lateral.z;
                double sideSign = lateralOffset >= 0 ? 1.0 : -1.0;

                living.hurt(dmgSrc, damage);

                double kbTravel;
                double kbLateral;
                double kbY;

                if (weight == WeightClass.LIGHT) {
                    kbTravel = 0.25 + Math.min(speed, 1.25) * 0.4;
                    kbLateral = (0.50 + Math.min(speed, 1.25) * 0.5) * sideSign;
                    kbY = 0.35 + Math.min(speed, 1.25) * 0.20;
                } else {
                    kbTravel = 0.20 + Math.min(speed, 1.25) * 0.3;
                    kbLateral = (0.38 + Math.min(speed, 1.25) * 0.4) * sideSign;
                    kbY = 0.20 + Math.min(speed, 1.25) * 0.12;
                }

                living.push(travelDir.x * kbTravel + lateral.x * kbLateral, kbY, travelDir.z * kbTravel + lateral.z * kbLateral);

                // Impact particles at contact point
                double impactX = (cart.getX() + living.getX()) * 0.5;
                double impactY = living.getY() + living.getBbHeight() * 0.5;
                double impactZ = (cart.getZ() + living.getZ()) * 0.5;
                sl.sendParticles(ParticleTypes.CRIT, impactX, impactY, impactZ, 12, 0.2, 0.2, 0.2, 0.15);
            }
            cart.needsSync = true;
        }
    }

    public static void handleCartToCartCollisions(TerracartEntity cart, double speed) {
        List<TerracartEntity> list = cart.level().getEntitiesOfClass(
                TerracartEntity.class,
                cart.getBoundingBox().inflate(0.15, 0.0, 0.15),
                e -> e != cart && !e.isRemoved()
        );

        if (list.isEmpty()) return;

        for (TerracartEntity other : list) {
            // Server: pair-wise guard so damage/sounds/particles process strictly once per unique pair
            if (!cart.level().isClientSide() && cart.getId() > other.getId()) continue;

            // Client: only process collisions for our own local vehicle so remote entities don't double-trigger
            if (cart.level().isClientSide()) {
                boolean isMyCart = cart.getControllingPassenger() instanceof Player player && player.isLocalPlayer();
                if (!isMyCart) continue;
            }

            // Anti-clipping horizontal separation
            double dx = other.getX() - cart.getX();
            double dz = other.getZ() - cart.getZ();
            double distSq = dx * dx + dz * dz;
            if (distSq < 1.0E-6) {
                dx = cart.getRandom().nextDouble() - 0.5;
                dz = cart.getRandom().nextDouble() - 0.5;
                distSq = dx * dx + dz * dz;
            }
            double dist = Math.sqrt(distSq);
            double nx = dx / dist;
            double nz = dz / dist;

            // Compute velocities & forward vectors using real movement deltas for remote entities
            double speedA = cart.getCurrentSpeed();
            double dxB = other.getX() - other.xOld;
            double dzB = other.getZ() - other.zOld;
            double moveSpeedB = Math.sqrt(dxB * dxB + dzB * dzB);
            double speedB = (Math.abs(other.getCurrentSpeed()) > 0.001) ? other.getCurrentSpeed() : moveSpeedB;

            double bpsA = Math.abs(speedA) * 20.0;
            double bpsB = Math.abs(speedB) * 20.0;

            float yawA = cart.getYRot();
            double radA = Math.toRadians(yawA);
            Vec3 forwardA = new Vec3(-Math.sin(radA), 0.0, Math.cos(radA));

            float yawB = other.getYRot();
            double radB = Math.toRadians(yawB);
            Vec3 forwardB = new Vec3(-Math.sin(radB), 0.0, Math.cos(radB));

            // Actual travel vectors
            Vec3 moveA = forwardA.scale(speedA);
            Vec3 moveB = forwardB.scale(bpsB / 20.0);

            // Relative velocity vector (Cart A relative to Cart B)
            Vec3 relVel = moveA.subtract(moveB);
            double relBps = relVel.horizontalDistance() * 20.0;

            // Heading alignment
            double dotHeading = forwardA.dot(forwardB);

            // Universal anti-clipping separation push along contact normal (minimal cushion to prevent overlap)
            double overlapPush = 0.02;
            cart.setDeltaMovement(cart.getDeltaMovement().add(-nx * overlapPush, 0.0, -nz * overlapPush));
            other.setDeltaMovement(other.getDeltaMovement().add(nx * overlapPush, 0.0, nz * overlapPush));

            // Cooldown & Stun check for collision events
            if (cart.getStunTicks() > 0 || other.getStunTicks() > 0 || cart.getCartCollisionCooldown() > 0 || other.getCartCollisionCooldown() > 0) continue;

            // ====================================================================================================
            // UNIFIED 2-STATE CART COLLISION SYSTEM: SAFE BUMP (< 8.0 BPS) vs CRITICAL CRASH (>= 8.0 BPS)
            // ====================================================================================================
            final double SAFE_SPEED_BPS = 8.0;

            if (relBps >= SAFE_SPEED_BPS) {
                // ================================================================================================
                // RULE 1: CRITICAL CRASH (High-Speed Impact)
                // ================================================================================================
                // Dynamic speed-scaled severity progress (0.0 at 8 BPS, 1.0 at 28+ BPS)
                double crashProgress = Mth.clamp((relBps - 8.0) / 20.0, 0.0, 1.0);
                float cartDamage = (float) Mth.lerp(crashProgress, 3.0, 10.0);
                boolean isSevere = relBps > 16.0;
                float playerDamage = isSevere ? 6.0F : 3.0F;

                // Dynamic speed-scaled rebound & launch impulse
                double reboundSpeed = Mth.lerp(crashProgress, 0.15, 0.35);
                double shoveSpeed = Mth.lerp(crashProgress, 0.20, 0.40);

                // Universal Stun (0.5s stoppage) & Hit Cooldown
                cart.setStunTicks(10);
                other.setStunTicks(10);
                cart.setCartCollisionCooldown(12);
                other.setCartCollisionCooldown(12);

                // Kill Ghost Throttle: Both engines stall to 0.0! (No phantom auto-drive after stun)
                cart.setCurrentSpeed(0.0);
                other.setCurrentSpeed(0.0);

                // Physical Rebound & Momentum Shove via pure deltaMovement
                boolean aIsFaster = (bpsA >= bpsB);
                TerracartEntity faster = aIsFaster ? cart : other;
                TerracartEntity slower = aIsFaster ? other : cart;
                Vec3 fasterFwd = aIsFaster ? forwardA : forwardB;
                double shoveDirX = aIsFaster ? nx : -nx;
                double shoveDirZ = aIsFaster ? nz : -nz;

                boolean isHeadOn = (dotHeading < -0.4 && bpsA >= 1.0 && bpsB >= 1.0);
                if (isHeadOn) {
                    // Symmetrical Head-On rebound: both carts bounce backward along their headings
                    cart.setDeltaMovement(-forwardA.x * reboundSpeed, 0.06, -forwardA.z * reboundSpeed);
                    other.setDeltaMovement(-forwardB.x * reboundSpeed, 0.06, -forwardB.z * reboundSpeed);
                } else {
                    // Momentum Shove: faster cart bounces back, slower/parked cart is launched away
                    faster.setDeltaMovement(-fasterFwd.x * reboundSpeed, 0.06, -fasterFwd.z * reboundSpeed);
                    slower.setDeltaMovement(shoveDirX * shoveSpeed, 0.08, shoveDirZ * shoveSpeed);
                }

                // Server-side universal crash consequences: damage, whiplash, audio, particles
                if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                    cart.setHealth(Math.max(0.0F, cart.getHealth() - cartDamage));
                    other.setHealth(Math.max(0.0F, other.getHealth() - cartDamage));

                    // Universal 3.0 HP (1.5 hearts) / 6.0 HP (3.0 hearts) driver whiplash damage
                    if (cart.getControllingPassenger() instanceof Player pA) {
                        pA.hurt(cart.damageSources().fall(), playerDamage);
                    }
                    if (other.getControllingPassenger() instanceof Player pB) {
                        pB.hurt(other.damageSources().fall(), playerDamage);
                    }

                    // Crash Sound & Particles (dense sparks + smoke puff)
                    double midX = (cart.getX() + other.getX()) * 0.5;
                    double midY = (cart.getY() + other.getY()) * 0.5 + 0.4;
                    double midZ = (cart.getZ() + other.getZ()) * 0.5;

                    sl.playSound(null, midX, midY, midZ, ModSounds.TERRACART_CRASH, SoundSource.NEUTRAL, 0.85F, 1.0F);
                    TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, 28, 0.08);
                    TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.POOF, 14, 0.04);
                    TerracartStateManager.spawnPerimeterParticles(sl, other, ParticleTypes.CRIT, 28, 0.08);
                    TerracartStateManager.spawnPerimeterParticles(sl, other, ParticleTypes.POOF, 14, 0.04);

                    if (cart.getHealth() <= 0.0F) TerracartStateManager.executeDestruction(cart, sl);
                    if (other.getHealth() <= 0.0F) TerracartStateManager.executeDestruction(other, sl);
                }
            } else {
                // ================================================================================================
                // RULE 2: SAFE BUMP (Bumper Cars & Low Speed under 8.0 BPS)
                // ================================================================================================
                // Universal 5-tick stun to prevent throttle overwrite and allow smooth physical recoil
                cart.setStunTicks(5);
                other.setStunTicks(5);
                cart.setCartCollisionCooldown(5);
                other.setCartCollisionCooldown(5);

                // Universal safe bump bounce impulse (0.12 B/t) - identical across all safe bumps
                double reboundSpeed = 0.12;
                cart.setDeltaMovement(-nx * reboundSpeed, 0.0, -nz * reboundSpeed);
                other.setDeltaMovement(nx * reboundSpeed, 0.0, nz * reboundSpeed);

                // Stall engines cleanly to 0.0 to prevent phantom propulsion
                cart.setCurrentSpeed(0.0);
                other.setCurrentSpeed(0.0);

                // Server-side universal bump consequences: 1.0 HP chassis damage, metallic clink sound, dense perimeter sparks
                if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
                    cart.setHealth(Math.max(0.0F, cart.getHealth() - 1.0F));
                    other.setHealth(Math.max(0.0F, other.getHealth() - 1.0F));

                    double midX = (cart.getX() + other.getX()) * 0.5;
                    double midY = (cart.getY() + other.getY()) * 0.5 + 0.4;
                    double midZ = (cart.getZ() + other.getZ()) * 0.5;

                    sl.playSound(null, midX, midY, midZ, ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 0.8F, 1.05F);
                    TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, 16, 0.05);
                    TerracartStateManager.spawnPerimeterParticles(sl, other, ParticleTypes.CRIT, 16, 0.05);

                    if (cart.getHealth() <= 0.0F) TerracartStateManager.executeDestruction(cart, sl);
                    if (other.getHealth() <= 0.0F) TerracartStateManager.executeDestruction(other, sl);

                    cart.needsSync = true;
                    other.needsSync = true;
                }
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
            dx *= inverse * 0.05;
            dz *= inverse * 0.05;

            // Push the entity away from the cart (cart is never pushed backward by entities)
            entity.push(dx, 0.0, dz);
        }
    }

    public static void checkWallCrash(TerracartEntity cart, Vec3 motion) {
        if (cart.getStunTicks() > 0 || cart.getHitCooldown() > 0) return;
        if (!cart.horizontalCollision) return;

        double horizSpeed = new Vec3(motion.x, 0.0, motion.z).horizontalDistance();
        double bps = horizSpeed * 20.0;

        // Determine travel direction vector (works for both forward drive and reverse gear)
        double dirX = horizSpeed > 0.001 ? motion.x / horizSpeed : -Math.sin(Math.toRadians(cart.getYRot()));
        double dirZ = horizSpeed > 0.001 ? motion.z / horizSpeed : Math.cos(Math.toRadians(cart.getYRot()));

        // Check if there is an impassable vertical obstacle directly in travel direction above 1-block step height (Y + 1.05 to Y + 1.6)
        // A tight probe focused directly at the bumper (radius 0.25) prevents false collisions with Step 2 on staircases
        double probeCenterX = cart.getX() + dirX * 1.25;
        double probeCenterZ = cart.getZ() + dirZ * 1.25;

        AABB probeBox = new AABB(
                probeCenterX - 0.25,
                cart.getY() + 1.05,
                probeCenterZ - 0.25,
                probeCenterX + 0.25,
                cart.getY() + 1.6,
                probeCenterZ + 0.25
        );

        boolean hasWallAhead = cart.level().getBlockCollisions(cart, probeBox).iterator().hasNext();
        if (!hasWallAhead) {
            // No solid wall above step height: low ground bump or natural hill step, not a wall crash!
            return;
        }

        // True wall confirmed! Now apply speed-based consequences:
        // Under 8 BPS: Low-speed bump / safe bumper bounce (0 damage, 0 stun)
        if (bps < 8.0) {
            applySafeBump(cart, dirX, dirZ);
            return;
        }

        // True Wall Crash Impact! (bps >= 8.0)
        boolean isSevere = bps > 16.0;
        float cartDamage = isSevere ? 6.0f + cart.getRandom().nextInt(4) : 3.0f + cart.getRandom().nextInt(4);
        float playerDamage = isSevere ? 6.0f : 3.0f; // 3 hearts (6 HP) if severe, 1.5 hearts (3 HP) if medium
        int fuelLossUnits = 2 + cart.getRandom().nextInt(3); // 2 to 4 FU

        applyImpactConsequences(cart, cartDamage, playerDamage, fuelLossUnits, isSevere, true, dirX, dirZ);
    }

    public static void applySafeBump(TerracartEntity cart, double dirX, double dirZ) {
        double reboundSpeed = 0.12;
        cart.setDeltaMovement(-dirX * reboundSpeed, 0.0, -dirZ * reboundSpeed);
        cart.setCurrentSpeed(0.0);
        cart.setStunTicks(5);
        cart.setHitCooldown(5);

        if (!cart.level().isClientSide() && cart.level() instanceof ServerLevel sl) {
            cart.setHealth(Math.max(0.0F, cart.getHealth() - 1.0F));

            sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 0.8F, 1.05F);
            TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, 16, 0.05);

            if (cart.getHealth() <= 0.0F) {
                TerracartStateManager.executeDestruction(cart, sl);
            }
            cart.needsSync = true;
        }
    }

    public static void applyImpactConsequences(TerracartEntity cart, float cartDamage, float playerDamage, int fuelLossUnits, boolean isSevere, boolean isWallCrash, double dirX, double dirZ) {
        // 1. Recoil (for wall & heavy mob crashes) - smooth physical rebound velocity impulse
        if (isWallCrash) {
            double reboundSpeed = 0.25;
            cart.setDeltaMovement(-dirX * reboundSpeed, 0.05, -dirZ * reboundSpeed);
            cart.setCurrentSpeed(-Math.signum(cart.getCurrentSpeed() != 0 ? cart.getCurrentSpeed() : 1.0) * reboundSpeed * 0.5);
        } else {
            cart.setCurrentSpeed(0.0);
            cart.setDeltaMovement(0.0, cart.getDeltaMovement().y, 0.0);
        }

        // 2. Control Stun & Hit Cooldown (0.5s = 10 ticks)
        cart.setStunTicks(10); // 0.5s stoppage time
        cart.setHitCooldown(10);

        if (cart.level().isClientSide()) return;

        // --- SERVER ONLY BELOW ---
        // 3. Fuel Loss (240 ticks per 1 FU)
        int currentFuel = cart.getFuel();
        cart.setFuel(Math.max(0, currentFuel - fuelLossUnits * 240));

        // 4. Cart Damage & Destruction Check
        cart.setHealth(cart.getHealth() - cartDamage);

        // 5. Player Damage in Survival
        if (cart.getControllingPassenger() instanceof Player player) {
            if (isWallCrash) {
                player.hurt(cart.damageSources().flyIntoWall(), playerDamage);
            } else {
                player.hurt(cart.damageSources().fall(), playerDamage);
            }
        }

        // 6. Sound & Particles
        if (cart.level() instanceof ServerLevel sl) {
            float soundPitch = isSevere ? 0.9F : 1.0F;
            float soundVol = isSevere ? 0.85F : 0.6F;
            sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), ModSounds.TERRACART_CRASH, SoundSource.NEUTRAL, soundVol, soundPitch);

            int critCount = isSevere ? 36 : 28;
            int poofCount = isSevere ? 20 : 12;
            TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, critCount, 0.08);
            TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.POOF, poofCount, 0.04);

            if (cart.getHealth() <= 0.0f) {
                TerracartStateManager.executeDestruction(cart, sl);
            }
            cart.needsSync = true;
        }
    }
}