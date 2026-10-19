package com.ilyrac.terracart.client.sound;

import com.ilyrac.terracart.entity.TerracartEntity;
import com.ilyrac.terracart.tier.TerracartTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;

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

        // Read server-synced core audio values
        float serverVol = cart.getSoundVolume();
        float serverPitch = cart.getSoundPitch();

        // Calculate absolute target volume combined with our distance fade modifier
        float targetVolume = 0.0f;
        if (cart.getEntityData().get(TerracartEntity.SOUND_ACTIVE)) {
            targetVolume = serverVol * distanceFadeFactor;
        }

        // Smoothly interpolate transitions (brisk 0.35f fade-in to eliminate dead air, gentle 0.15f fade-out)
        float lerpRate = (targetVolume > this.volume) ? 0.35f : 0.15f;
        this.volume += (targetVolume - this.volume) * lerpRate;
        this.pitch  += (serverPitch - this.pitch) * lerpRate;

        if (this.volume < 0.005f) {
            this.volume = 0.0f;
        }

        // Track spatial positions seamlessly
        this.x = cart.getX();
        this.y = cart.getY();
        this.z = cart.getZ();
    }
}