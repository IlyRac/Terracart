package com.ilyrac.terracart.client.sound;

import com.ilyrac.terracart.ModSounds;
import com.ilyrac.terracart.entity.TerracartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;

public class TerracartSoundInstance extends AbstractTickableSoundInstance {
    private final TerracartEntity cart;

    public TerracartSoundInstance(TerracartEntity cart) {
        super(ModSounds.TERRACART_MOVING, SoundSource.NEUTRAL, cart.level().getRandom());
        this.cart = cart;
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

        // Smoothly interpolate transitions (handles both fade-out AND fade-in)
        final float CLIENT_LERP = 0.15f;
        this.volume += (targetVolume - this.volume) * CLIENT_LERP;
        this.pitch  += (serverPitch - this.pitch) * CLIENT_LERP;

        // Track spatial positions seamlessly
        this.x = cart.getX();
        this.y = cart.getY();
        this.z = cart.getZ();
    }
}