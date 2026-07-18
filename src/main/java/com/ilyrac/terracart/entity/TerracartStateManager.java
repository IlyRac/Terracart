package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;

public class TerracartStateManager {

    public static void burnFuel(TerracartEntity cart) {
        int fuel = cart.getFuel();
        boolean isMoving = cart.getDeltaMovement().horizontalDistanceSqr() > 0.0001;
        if (fuel > 0 && isMoving) {
            cart.setFuel(fuel - 1);
        }
    }

    public static void handleHazards(TerracartEntity cart) {
        if (cart.level().isClientSide() || cart.getFireCooldown() > 0) return;

        BlockPos pos = cart.blockPosition();
        BlockState state = cart.level().getBlockState(pos);
        boolean inFireBlock = state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE) || state.is(Blocks.CAMPFIRE);

        if (inFireBlock) {
            cart.hurtServer((ServerLevel) cart.level(), cart.damageSources().inFire(), 5.0f);
            cart.setFireCooldown(15);
        } else if (cart.isInLava()) {
            cart.hurtServer((ServerLevel) cart.level(), cart.damageSources().lava(), 10.0f);
            cart.setFireCooldown(20);
        }
    }

    public static void updateSoundState(TerracartEntity cart, double speed) {
        boolean active = speed > 0.01 && cart.onGround();
        float targetVolume = active ? Mth.clamp((float) (speed * 2.0), 0.0F, 1.0F) : 0.0F;
        float targetPitch = active ? 1.0F + Mth.clamp((float) (speed * 0.6), 0.0F, 1.0F) : 1.0F;

        float vol = cart.getSoundVolume();
        float pit = cart.getSoundPitch();

        vol += (targetVolume - vol) * 0.15f;
        pit += (targetPitch - pit) * 0.15f;
        if (Math.abs(vol) < 0.0005f) vol = 0.0f;

        cart.setSoundActive(active);
        cart.setSoundVolume(vol);
        cart.setSoundPitch(pit);
    }

    public static void spawnMovementParticles(TerracartEntity cart, double dx, double dz, double speed) {
        if (!cart.onGround() || !(cart.level() instanceof ServerLevel sl)) return;
        if (speed < 0.002) return;

        int count = Mth.clamp((int) (speed * 1800), 4, 20);
        double dirX = (speed > 0.0001) ? dx / speed : cart.getDeltaMovement().x;
        double dirZ = (speed > 0.0001) ? dz / speed : cart.getDeltaMovement().z;

        for (int i = 0; i < count; i++) {
            sl.sendParticles(ParticleTypes.SMOKE,
                    cart.getX() + (cart.getRandom().nextDouble() - 0.5) * 1.2,
                    cart.getY() + 0.05,
                    cart.getZ() + (cart.getRandom().nextDouble() - 0.5) * 1.2,
                    1, -dirX * 0.3, 0.03, -dirZ * 0.3, 0.0);
        }
    }

    public static void executeDestruction(TerracartEntity cart, ServerLevel level) {
        cart.kill(level);
        if (!level.getGameRules().get(GameRules.ENTITY_DROPS)) return;

        spawnDrop(level, cart, new ItemStack(Items.IRON_INGOT, 3 + cart.getRandom().nextInt(5)));
        spawnDrop(level, cart, new ItemStack(ModItems.TERRRACART_WHEEL));
        spawnDrop(level, cart, new ItemStack(Items.FURNACE));
    }

    private static void spawnDrop(ServerLevel level, Entity entity, ItemStack stack) {
        ItemEntity item = new ItemEntity(level, entity.getX(), entity.getY() + 0.5D, entity.getZ(), stack);
        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
    }
}