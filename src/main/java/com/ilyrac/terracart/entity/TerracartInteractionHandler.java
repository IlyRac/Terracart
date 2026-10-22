package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import com.ilyrac.terracart.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;


public class TerracartInteractionHandler {

    public static InteractionResult handleInteraction(TerracartEntity cart, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 1. Name Tag Handling
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

        // 1. Repair Mechanic (+25 HP fixed point)
        // Prevent wasting an iron ingot if missing less than 5 HP (nearly/fully repaired)
        if (stack.is(Items.IRON_INGOT)) {
            if (cart.getMaxHealth() - cart.getHealth() < 5.0f) {
                player.sendOverlayMessage(Component.literal("Terracart is already fully repaired."));
                return InteractionResult.SUCCESS;
            }
            cart.setHealth(cart.getHealth() + 25.0f);
            playSpecialEffect(cart, ModSounds.TERRACART_REPAIR, ParticleTypes.EGG_CRACK, 28);
            if (!player.isCreative()) stack.shrink(1);
            player.sendOverlayMessage(Component.literal("Terracart repaired (+25 HP)"));
            return InteractionResult.SUCCESS;
        }

        // 2. Refueling Mechanic (+25 FU fixed point = 25 * 240 ticks)
        // Prevent wasting coal/charcoal if missing less than 5 FU (nearly/fully fueled)
        if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) {
            if (cart.getMaxFuel() - cart.getFuel() < 5 * 240) {
                player.sendOverlayMessage(Component.literal("Fuel tank is already full!"));
                return InteractionResult.SUCCESS;
            }
            cart.setFuel(cart.getFuel() + (25 * 240));
            playSpecialEffect(cart, ModSounds.TERRACART_REFUEL, ParticleTypes.CAMPFIRE_COSY_SMOKE, 18);
            if (!player.isCreative()) stack.shrink(1);
            player.sendOverlayMessage(Component.literal("Terracart refueled (+25 FU)"));
            return InteractionResult.SUCCESS;
        }

        // 3. Paint Stripping with Wet Sponge
        if (stack.is(Items.WET_SPONGE)) {
            if (cart.getCartColor() == -1) {
                if (!cart.level().isClientSide()) {
                    player.sendOverlayMessage(Component.literal("Terracart is already in factory paint."));
                }
                return InteractionResult.SUCCESS;
            }

            if (!cart.level().isClientSide()) {
                cart.setCartColor(-1);
                playSpecialEffect(cart, SoundEvents.BUCKET_EMPTY, ParticleTypes.SPLASH, 36);

                // Both Creative and Survival: Wet Sponge turns into Dry Sponge
                if (stack.getCount() == 1) {
                    player.setItemInHand(hand, new ItemStack(Items.SPONGE));
                } else {
                    stack.shrink(1);
                    ItemStack drySponge = new ItemStack(Items.SPONGE);
                    if (!player.getInventory().add(drySponge)) {
                        if (cart.level() instanceof ServerLevel sl) {
                            sl.addFreshEntity(new ItemEntity(sl, player.getX(), player.getY(), player.getZ(), drySponge));
                        }
                    }
                }
                player.sendOverlayMessage(Component.literal("Paint stripped back to factory gray."));
            }
            return InteractionResult.SUCCESS;
        }

        // 4. Painting with Dye
        DyeColor dyeColor = stack.get(DataComponents.DYE);
        if (dyeColor != null) {
            // Must strip existing paint first
            if (cart.getCartColor() != -1) {
                if (!cart.level().isClientSide()) {
                    player.sendOverlayMessage(Component.literal("Strip the existing paint with a Wet Sponge first!"));
                }
                return InteractionResult.SUCCESS;
            }

            // Cannot apply Gray Dye to default factory paint (which is already gray)
            if (dyeColor == DyeColor.GRAY) {
                if (!cart.level().isClientSide()) {
                    player.sendOverlayMessage(Component.literal("This Terracart is already in its default factory gray!"));
                }
                return InteractionResult.SUCCESS;
            }

            if (!cart.level().isClientSide()) {
                cart.setCartColor(dyeColor.getId());
                playSpecialEffect(cart, SoundEvents.DYE_USE, new ItemParticleOption(ParticleTypes.ITEM, stack.getItem()), 32);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                String colorName = dyeColor.getName().replace('_', ' ');
                String formattedColor = Character.toUpperCase(colorName.charAt(0)) + colorName.substring(1);
                player.sendOverlayMessage(Component.literal("Terracart painted " + formattedColor + "."));
            }
            return InteractionResult.SUCCESS;
        }

        // 5. Mount Passenger

        if (cart.canAddPassenger(player) && cart.getPassengers().isEmpty()) {
            if (!cart.level().isClientSide()) {
                player.startRiding(cart);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    // Moved damage logic here
    public static boolean handleHurtServer(TerracartEntity cart, ServerLevel sl, DamageSource src, float amount) {
        // Clean and safe: no more 'isInvulnerable' errors here!
        if (cart.isRemoved()) return false;

        // 1. 100% 1:1 Damage Ratio (1 damage = 1 condition HP)
        cart.setHealth(Math.max(0.0F, cart.getHealth() - amount));

        // 2. Physical Impulse, Feedback & Stun: Explosion Blast Wave vs Subtle Melee Nudge
        if (src.is(DamageTypeTags.IS_EXPLOSION)) {
            // Generic crash logic for blasts: crash sound, crit & poof particles, and 10-tick freeze stun
            float soundPitch = amount > 20.0F ? 0.9F : 1.0F;
            sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), ModSounds.TERRACART_CRASH, SoundSource.NEUTRAL, 0.85F, soundPitch);
            TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, 32, 0.08);
            TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.POOF, 18, 0.04);

            net.minecraft.world.entity.Entity sourceEntity = src.getDirectEntity() != null ? src.getDirectEntity() : src.getEntity();
            Vec3 blastCenter = src.getSourcePosition() != null ? src.getSourcePosition() : (sourceEntity != null ? sourceEntity.position() : null);
            Vec3 blastDir;
            if (blastCenter != null) {
                Vec3 diff = cart.position().subtract(blastCenter);
                blastDir = diff.horizontalDistanceSqr() > 1.0E-4 
                        ? new Vec3(diff.x, 0.0, diff.z).normalize() 
                        : new Vec3(cart.getRandom().nextDouble() - 0.5, 0.0, cart.getRandom().nextDouble() - 0.5).normalize();
            } else {
                blastDir = new Vec3(cart.getRandom().nextDouble() - 0.5, 0.0, cart.getRandom().nextDouble() - 0.5).normalize();
            }
            double blastForce = Math.min(1.0, 0.35 + (amount / 40.0) * 0.45);
            double blastY = Math.clamp(0.32 + (amount / 40.0) * 0.24, 0.32, 0.56);
            cart.setDeltaMovement(blastDir.x * blastForce, blastY, blastDir.z * blastForce);
            cart.setCurrentSpeed(0.0);
            cart.setStunTicks(10);
            cart.setHitCooldown(10);
            cart.needsSync = true;
        } else {
            // Generic melee / projectile hit: hit clink and subtle crit sparks
            sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 1.0F, 1.0F + (cart.getRandom().nextFloat() - 0.5F) * 0.2F);
            TerracartStateManager.spawnPerimeterParticles(sl, cart, ParticleTypes.CRIT, 16, 0.08);

            if (src.getEntity() != null) {
                Vec3 diff = cart.position().subtract(src.getEntity().position());
                Vec3 hitDir = diff.horizontalDistanceSqr() > 1.0E-4 
                        ? new Vec3(diff.x, 0.0, diff.z).normalize() 
                        : new Vec3(0.0, 0.0, 1.0);
                double nudge = 0.06;
                cart.setDeltaMovement(hitDir.x * nudge, 0.0, hitDir.z * nudge);
                cart.needsSync = true;
            }
        }

        if (cart.getHealth() <= 0) {
            TerracartStateManager.executeDestruction(cart, sl);
        }
        return true;
    }

    private static void playSpecialEffect(TerracartEntity cart, SoundEvent sound, ParticleOptions particle, int count) {
        if (cart.level() instanceof ServerLevel sl) {
            sl.playSound(null, cart.getX(), cart.getY(), cart.getZ(), sound, SoundSource.BLOCKS, 1.0f, 1.0f + (cart.getRandom().nextFloat() - 0.5f) * 0.15f);
            TerracartStateManager.spawnPerimeterParticles(sl, cart, particle, count, 0.05);
        }
    }

    // Moved from TerracartEntity
    public static void handleDestroy(TerracartEntity cart, ServerLevel sl, DamageSource src) {
        TerracartStateManager.executeDestruction(cart, sl);
    }

    // Moved from TerracartEntity
    public static ItemStack handlePickResult(TerracartEntity cart) {
        ItemStack stack = new ItemStack(ModItems.getCartItemFor(cart.getTier()));
        stack.set(com.ilyrac.terracart.component.ModDataComponents.CORE_STATS, cart.getCoreStats());
        return stack;
    }
}
