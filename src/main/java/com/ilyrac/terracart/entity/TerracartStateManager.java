package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import com.ilyrac.terracart.item.ModItems;
import com.ilyrac.terracart.tier.TerracartTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;

public class TerracartStateManager {

    public static void burnFuel(TerracartEntity cart) {
        int fuel = cart.getFuel();
        boolean isMoving = cart.getDeltaMovement().horizontalDistanceSqr() > 0.0001;
        if (fuel > 0 && isMoving) {
            int newFuel = fuel - 1;
            cart.setFuelLocal(newFuel);
            if (!cart.level().isClientSide()) {
                if (newFuel == 0 || cart.tickCount % 20 == 0) {
                    cart.syncFuel();
                }
                if (newFuel == 0) {
                    cart.level().playSound(null, cart.getX(), cart.getY(), cart.getZ(),
                            ModSounds.TERRACART_ENGINE_SPUTTERS, SoundSource.NEUTRAL, 1.0f, 1.0f);
                    if (cart.level() instanceof ServerLevel sl) {
                        spawnPerimeterParticles(sl, cart, new DustParticleOptions(0x111111, 1.2f), 28, 0.03);
                    }
                }
            }
        }
    }

    public static void handleHazards(TerracartEntity cart) {
        if (cart.level().isClientSide()) return;

        double waterHeight = cart.getFluidHeight(FluidTags.WATER);
        if (waterHeight > 1.05 && cart.level() instanceof ServerLevel sl) {
            // Deep water: gentle perimeter bubble plume rising along the hull outline
            if (cart.tickCount % 4 == 0) {
                spawnPerimeterParticles(sl, cart, ParticleTypes.BUBBLE, 2, 0.02);
            }
        }

        // Suppress vanilla fire overlay & spawn burning vehicle smoke and embers
        if (cart.getRemainingFireTicks() > 0) {
            cart.clearFire();
            if (cart.level() instanceof ServerLevel sl && cart.tickCount % 4 == 0) {
                spawnPerimeterParticles(sl, cart, ParticleTypes.SMOKE, 6, 0.03);
                spawnPerimeterParticles(sl, cart, ParticleTypes.CAMPFIRE_COSY_SMOKE, 3, 0.02);
            }
        }

        if (cart.getFireCooldown() > 0) return;

        // Check full bounding box for any fire blocks
        boolean inFireBlock = false;
        AABB box = cart.getBoundingBox();
        int minX = Mth.floor(box.minX), maxX = Mth.floor(box.maxX);
        int minY = Mth.floor(box.minY), maxY = Mth.floor(box.maxY);
        int minZ = Mth.floor(box.minZ), maxZ = Mth.floor(box.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockState bs = cart.level().getBlockState(new BlockPos(x, y, z));
                    if (bs.is(Blocks.FIRE) || bs.is(Blocks.SOUL_FIRE) || bs.is(Blocks.CAMPFIRE) || bs.is(Blocks.SOUL_CAMPFIRE)) {
                        inFireBlock = true;
                        break;
                    }
                }
                if (inFireBlock) break;
            }
            if (inFireBlock) break;
        }

        if (inFireBlock) {
            if (cart.level() instanceof ServerLevel sl) {
                sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.FIRE_AMBIENT, SoundSource.NEUTRAL, 0.85F, 1.0F);
                spawnPerimeterParticles(sl, cart, ParticleTypes.SMOKE, 8, 0.04);
                spawnPerimeterParticles(sl, cart, ParticleTypes.CAMPFIRE_COSY_SMOKE, 4, 0.03);
                spawnPerimeterParticles(sl, cart, ParticleTypes.FLAME, 6, 0.05);
            }
            cart.hurtServer((ServerLevel) cart.level(), cart.damageSources().inFire(), 5.0f);
            cart.setFireCooldown(15);
        } else if (cart.isInLava()) {
            if (cart.level() instanceof ServerLevel sl) {
                // Audio-visual feedback: sizzle extinguish & billowing steam plumes + embers
                sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 0.85F, 0.9F + cart.getRandom().nextFloat() * 0.2F);
                spawnPerimeterParticles(sl, cart, ParticleTypes.CAMPFIRE_COSY_SMOKE, 10, 0.04);
                spawnPerimeterParticles(sl, cart, ParticleTypes.LAVA, 6, 0.08);
            }
            cart.hurtServer((ServerLevel) cart.level(), cart.damageSources().lava(), 10.0f);
            cart.setFireCooldown(20);
        }
    }

    public static void updateSoundState(TerracartEntity cart, double speed) {
        boolean active = speed > 0.005 && cart.onGround() && cart.hasFuel();
        cart.setSoundActive(active);
    }

    public static void spawnMovementParticles(TerracartEntity cart, double dx, double dz, double speed) {
        if (!cart.level().isClientSide()) return; // Purely client-side! Zero server packets!
        if ((!cart.onGround() && cart.getFluidHeight(FluidTags.WATER) <= 0.0)) return;
        if (speed < 0.01) return;

        double dirX = (speed > 0.0001) ? dx / speed : cart.getDeltaMovement().x;
        double dirZ = (speed > 0.0001) ? dz / speed : cart.getDeltaMovement().z;

        float yawRad = (float) Math.toRadians(cart.getYRot());
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);
        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);

        // 4 wheel coordinates relative to center (forward/back, left/right)
        final double[][] wheels = {
            { 0.85, -0.60 }, // Front-Left
            { 0.85,  0.60 }, // Front-Right
            {-0.85, -0.60 }, // Rear-Left
            {-0.85,  0.60 }  // Rear-Right
        };

        // Scale chance per wheel: light at slow speeds, up to ~75% chance per wheel at cruising speed
        float emitChance = Mth.clamp((float) (speed * 2.5), 0.20F, 0.75F);
        boolean inWater = cart.getFluidHeight(FluidTags.WATER) > 0.0;

        for (double[] wheel : wheels) {
            if (cart.getRandom().nextFloat() > emitChance) continue;

            double wx = cart.getX() + wheel[0] * forwardX + wheel[1] * rightX + (cart.getRandom().nextDouble() - 0.5) * 0.1;
            double wy = cart.getY() + 0.08;
            double wz = cart.getZ() + wheel[0] * forwardZ + wheel[1] * rightZ + (cart.getRandom().nextDouble() - 0.5) * 0.1;

            if (inWater) {
                cart.level().addParticle(ParticleTypes.SPLASH, wx, wy + 0.1, wz, -dirX * 0.12, 0.18, -dirZ * 0.12);
                if (cart.getRandom().nextBoolean()) {
                    cart.level().addParticle(ParticleTypes.BUBBLE, wx, wy, wz, -dirX * 0.08, 0.08, -dirZ * 0.08);
                }
            } else {
                cart.level().addParticle(ParticleTypes.SMOKE, wx, wy, wz, -dirX * 0.18, 0.02, -dirZ * 0.18);
            }
        }
    }

    public static void spawnPerimeterParticles(ServerLevel level, TerracartEntity cart, ParticleOptions particle, int count, double speed) {
        float yawRad = (float) Math.toRadians(cart.getYRot());
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);
        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);

        final double halfLength = 1.25;
        final double halfWidth = 0.65;
        final double frontLen = halfWidth * 2; // 1.3
        final double sideLen = halfLength * 2;  // 2.5
        final double totalPerimeter = (frontLen + sideLen) * 2; // 7.6

        for (int i = 0; i < count; i++) {
            double d = cart.getRandom().nextDouble() * totalPerimeter;
            double u, v;
            if (d < frontLen) {
                u = halfLength;
                v = -halfWidth + d;
            } else if (d < frontLen + sideLen) {
                v = halfWidth;
                u = halfLength - (d - frontLen);
            } else if (d < frontLen * 2 + sideLen) {
                u = -halfLength;
                v = halfWidth - (d - (frontLen + sideLen));
            } else {
                v = -halfWidth;
                u = -halfLength + (d - (frontLen * 2 + sideLen));
            }

            double px = cart.getX() + u * forwardX + v * rightX;
            double py = cart.getY() + 0.85 + (cart.getRandom().nextDouble() - 0.5) * 0.25;
            double pz = cart.getZ() + u * forwardZ + v * rightZ;

            level.sendParticles(particle, px, py, pz, 1, 0.04, 0.04, 0.04, speed);
        }
    }

    public static void executeDestruction(TerracartEntity cart, ServerLevel level) {
        level.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.65F, 1.1F + (cart.getRandom().nextFloat() - 0.5F) * 0.2F);
        level.sendParticles(ParticleTypes.EXPLOSION, cart.getX(), cart.getY() + 0.5, cart.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, Items.IRON_INGOT), cart.getX(), cart.getY() + 0.5, cart.getZ(), 16, 0.4, 0.3, 0.4, 0.15);

        cart.kill(level);
        if (!level.getGameRules().get(GameRules.ENTITY_DROPS)) return;

        spawnDrop(level, cart, new ItemStack(Items.IRON_INGOT, 2 + cart.getRandom().nextInt(3)));
        spawnDrop(level, cart, new ItemStack(ModItems.TERRRACART_WHEEL));

        // Calibrated Tier Core retaining exact rolled stats from the vehicle
        ItemStack coreStack = new ItemStack(ModItems.getCoreItemFor(cart.getTier()));
        coreStack.set(com.ilyrac.terracart.component.ModDataComponents.CORE_STATS, cart.getCoreStats());
        spawnDrop(level, cart, coreStack);
    }

    private static void spawnDrop(ServerLevel level, Entity entity, ItemStack stack) {
        ItemEntity item = new ItemEntity(level, entity.getX(), entity.getY() + 0.5D, entity.getZ(), stack);
        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
    }
}
