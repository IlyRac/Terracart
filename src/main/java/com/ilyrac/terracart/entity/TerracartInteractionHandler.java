package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import com.ilyrac.terracart.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class TerracartInteractionHandler {

    public static InteractionResult handleInteraction(TerracartEntity cart, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 1. Repair Mechanic
        if (stack.is(Items.IRON_INGOT)) {
            if (cart.getHealth() >= TerracartEntity.MAX_HEALTH) {
                player.sendOverlayMessage(Component.literal("Terracart is already fully repaired."));
                return InteractionResult.SUCCESS;
            }
            cart.setHealth(cart.getHealth() + (TerracartEntity.MAX_HEALTH * 0.25f));
            playSpecialEffect(cart, ModSounds.TERRACART_REPAIR, ParticleTypes.EGG_CRACK, 12, 1f);
            if (!player.isCreative()) stack.shrink(1);
            player.sendOverlayMessage(Component.literal("Terracart repaired (+25%)"));
            return InteractionResult.SUCCESS;
        }

        // 2. Refueling Mechanic
        if (stack.is(Items.COAL)) {
            if (cart.getFuel() > (TerracartEntity.MAX_FUEL * 0.90)) {
                player.sendOverlayMessage(Component.literal("Fuel tank is already full!"));
                return InteractionResult.SUCCESS;
            }
            if (cart.getFuel() > (TerracartEntity.MAX_FUEL * 0.80)) {
                player.sendOverlayMessage(Component.literal("Fuel tank is nearly full!"));
                return InteractionResult.SUCCESS;
            }
            cart.setFuel(Math.min(cart.getFuel() + (int) (TerracartEntity.MAX_FUEL * 0.25), TerracartEntity.MAX_FUEL));
            playSpecialEffect(cart, ModSounds.TERRACART_REFUEL, ParticleTypes.CAMPFIRE_COSY_SMOKE, 6, 0.1f);
            if (!player.isCreative()) stack.shrink(1);
            player.sendOverlayMessage(Component.literal("TerraCart refueled (+25%)"));
            return InteractionResult.SUCCESS;
        }

        // 3. Mount Passenger
        if (cart.canAddPassenger(player) && cart.getPassengers().isEmpty()) {
            if (!cart.level().isClientSide()) {
                player.startRiding(cart);
            }
            return InteractionResult.SUCCESS;
        }

        // 4. Name Tag Handling
        if (stack.is(Items.NAME_TAG)) {
            Component customName = stack.get(DataComponents.CUSTOM_NAME);
            if (customName != null) {
                if (!cart.level().isClientSide()) {
                    cart.setCustomName(customName);
                    // CHANGE THIS TO FALSE:
                    cart.setCustomNameVisible(false);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    // Moved damage logic here
    public static boolean handleHurtServer(TerracartEntity cart, ServerLevel sl, float amount) {
        // Clean and safe: no more 'isInvulnerable' errors here!
        if (cart.isRemoved()) return false;

        cart.setHealth(cart.getHealth() - (amount * 0.7f));
        sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 1.0F, 1.0F + (cart.getRandom().nextFloat() - 0.5F) * 0.2F);
        sl.sendParticles(ParticleTypes.SQUID_INK, cart.getX(), cart.getY() + 1, cart.getZ(), 5, 0.1, 0.1, 0.1, 0.1);

        if (cart.getHealth() <= 0) {
            TerracartStateManager.executeDestruction(cart, sl);
        }
        return true;
    }

    private static void playSpecialEffect(TerracartEntity cart, net.minecraft.sounds.SoundEvent sound, net.minecraft.core.particles.SimpleParticleType particle, int count, float speed) {
        if (cart.level() instanceof ServerLevel sl) {
            sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), sound, SoundSource.BLOCKS, 1.0f, 1.0f + (cart.getRandom().nextFloat() - 0.5f) * 0.15f);
            sl.sendParticles(particle, cart.getX(), cart.getY() + 0.6, cart.getZ(), count, 0.25, 0.25, 0.25, speed);
        }
    }

    // Moved from TerracartEntity
    public static void handleDestroy(TerracartEntity cart, ServerLevel sl, DamageSource src) {
        TerracartStateManager.executeDestruction(cart, sl);
    }

    // Moved from TerracartEntity
    public static ItemStack handlePickResult(TerracartEntity cart) {
        int color = cart.getCartColor();
        if (color >= 0 && color < ModItems.COLORED_TERRACARTS.length) {
            return new ItemStack(ModItems.COLORED_TERRACARTS[color]);
        }
        return new ItemStack(ModItems.TERRACART);
    }
}