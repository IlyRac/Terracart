package com.ilyrac.terracart.sound;

import com.ilyrac.terracart.ModSounds;
import com.ilyrac.terracart.entity.TerraCartEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;

public class TerraCartSoundInstance extends AbstractTickableSoundInstance {
    private final TerraCartEntity cart;

    public TerraCartSoundInstance(TerraCartEntity cart) {
        super(ModSounds.TERRACART_SOUND, SoundSource.NEUTRAL, cart.level().getRandom());
        this.cart = cart;
        this.looping = true;
        this.delay = 0;
        this.x = cart.getX();
        this.y = cart.getY();
        this.z = cart.getZ();
        this.pitch = 0.8f;
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

        // read server-synced values
        float serverVol = cart.getEntityData().get(com.ilyrac.terracart.entity.TerraCartEntity.SOUND_VOLUME);
        float serverPitch = cart.getEntityData().get(com.ilyrac.terracart.entity.TerraCartEntity.SOUND_PITCH);

        // never reach absolute zero
        final float MIN_VOL = 0.001f;

        float targetVolume = Math.max(serverVol, MIN_VOL);

        // tweak factor
        final float CLIENT_LERP = 0.8f;
        this.volume += (targetVolume - this.volume) * CLIENT_LERP;
        this.pitch  += (serverPitch - this.pitch) * CLIENT_LERP;

        // follow the cart position every tick
        this.x = cart.getX();
        this.y = cart.getY();
        this.z = cart.getZ();
    }
}