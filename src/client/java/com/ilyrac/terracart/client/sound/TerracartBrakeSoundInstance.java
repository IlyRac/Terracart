package com.ilyrac.terracart.client.sound;

import com.ilyrac.terracart.ModSounds;
import com.ilyrac.terracart.entity.TerracartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class TerracartBrakeSoundInstance extends AbstractTickableSoundInstance {
    private final TerracartEntity cart;

    public TerracartBrakeSoundInstance(TerracartEntity cart) {
        super(ModSounds.TERRACART_BRAKE, SoundSource.NEUTRAL, cart.level().getRandom());
        this.cart = cart;
        this.looping = true;
        this.delay = 0;
        this.relative = false;

        this.x = cart.getX();
        this.y = cart.getY();
        this.z = cart.getZ();

        this.volume = 0.0f;
        this.pitch = 1.0f;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (this.cart == null || this.cart.isRemoved()) {
            this.stop();
            return;
        }

        this.x = this.cart.getX();
        this.y = this.cart.getY();
        this.z = this.cart.getZ();

        Minecraft mc = Minecraft.getInstance();
        float distanceFadeFactor = 1.0f;

        if (mc.player != null) {
            double dist = Math.sqrt(this.cart.distanceToSqr(mc.player));
            if (dist > 16.0) {
                distanceFadeFactor = (float) ((64.0 - dist) / (64.0 - 16.0));
                if (distanceFadeFactor < 0.0f) distanceFadeFactor = 0.0f;
            }
        }

        // Fast responsive attack (0.45f fade-in), smooth clean release (0.25f fade-out)
        float targetVolume = this.cart.isBrakingActive() ? 0.65f * distanceFadeFactor : 0.0f;
        float lerpRate = (targetVolume > this.volume) ? 0.45f : 0.25f;
        this.volume += (targetVolume - this.volume) * lerpRate;

        if (!this.cart.isBrakingActive() && this.volume < 0.01f) {
            this.stop();
            return;
        }

        // Dynamic pitch modulation as cart decelerates to a stop
        double speedBps = this.cart.getSpeedBps();
        float targetPitch = (float) Mth.clamp(0.85 + (speedBps / 20.0) * 0.3, 0.85, 1.15);
        this.pitch += (targetPitch - this.pitch) * 0.3f;
    }
}
