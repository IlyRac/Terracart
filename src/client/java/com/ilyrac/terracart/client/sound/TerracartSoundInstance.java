package com.ilyrac.terracart.client.sound;

import com.ilyrac.terracart.entity.TerracartEntity;
import com.ilyrac.terracart.tier.TerracartTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class TerracartSoundInstance extends AbstractTickableSoundInstance {
    private final TerracartEntity cart;
    private final TerracartTier tier;

    public TerracartSoundInstance(TerracartEntity cart) {
        super(cart.getTier().getMovingSound(), SoundSource.NEUTRAL, cart.level().getRandom());
        this.cart = cart;
        this.tier = cart.getTier();
        this.looping = true;
        this.delay = 0;
        this.relative = false;

        // Exact initial position sync
        this.x = cart.getX();
        this.y = cart.getY();
        this.z = cart.getZ();

        this.pitch = 0.8f;
        this.volume = 0.0f;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (cart == null || cart.isRemoved()) {
            super.stop();
            return;
        }

        // If tier changed, stop so controller can recreate the sound instance with the new tier sound
        if (cart.getTier() != this.tier) {
            super.stop();
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        float distanceFadeFactor = 1.0f;

        if (mc.player != null) {
            double dist = Math.sqrt(cart.distanceToSqr(mc.player));

            // Smooth linear tracking spanning from 16 blocks (full volume) to 64 blocks (silence)
            if (dist > 16.0) {
                distanceFadeFactor = (float) ((64.0 - dist) / (64.0 - 16.0));
                if (distanceFadeFactor < 0.0f) distanceFadeFactor = 0.0f;
            }
        }

        // Pure client-side dynamic audio calculations (zero network packets)
        boolean active = cart.isSoundActive();
        float maxSpeed = cart.getCoreStats().maxSpeed() / 20.0F;
        double speed = cart.getDeltaMovement().horizontalDistance();
        float speedRatio = maxSpeed > 0.001F ? (float) (speed / maxSpeed) : 0.0F;
        speedRatio = Mth.clamp(speedRatio, 0.0F, 1.0F);

        float targetVolume = 0.0F;
        float targetPitch = 0.85F;

        if (active) {
            targetVolume = (0.25F + speedRatio * 0.75F) * distanceFadeFactor;
            if (speedRatio < 0.60F) {
                targetPitch = 0.95F + (speedRatio / 0.60F) * 0.20F;
            } else {
                float highT = (speedRatio - 0.60F) / 0.40F;
                targetPitch = 1.15F + (highT * highT) * 0.40F;
            }
        }

        // Smoothly interpolate transitions (brisk 0.35f fade-in to eliminate dead air, gentle 0.15f fade-out)
        float lerpRate = (targetVolume > this.volume) ? 0.35f : 0.15f;
        this.volume += (targetVolume - this.volume) * lerpRate;
        this.pitch  += (targetPitch - this.pitch) * lerpRate;

        if (this.volume < 0.005f) {
            this.volume = 0.0f;
        }

        // Track spatial positions seamlessly
        this.x = cart.getX();
        this.y = cart.getY();
        this.z = cart.getZ();
    }
}
