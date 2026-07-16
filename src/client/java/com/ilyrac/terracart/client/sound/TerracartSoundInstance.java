package com.ilyrac.terracart.client.sound;

import com.ilyrac.terracart.ModSounds;
import com.ilyrac.terracart.entity.TerracartEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;

public class TerracartSoundInstance extends AbstractTickableSoundInstance {
    private final TerracartEntity cart;

    public TerracartSoundInstance(TerracartEntity cart) {
        super(ModSounds.TERRACART_MOVING, SoundSource.NEUTRAL, cart.level().getRandom());
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
        float serverVol = cart.getSoundVolume();
        float serverPitch = cart.getSoundPitch();

        // Only enforce minimum volume if the cart is supposed to make sound
        float targetVolume = 0.0f;
        if (cart.getEntityData().get(TerracartEntity.SOUND_ACTIVE)) {
            targetVolume = serverVol;
        }
        // tweak factor
        final float CLIENT_LERP = 0.15f;
        this.volume += (targetVolume - this.volume) * CLIENT_LERP;
        this.pitch  += (serverPitch - this.pitch) * CLIENT_LERP;

        // follow the cart position every tick
        this.x = cart.getX();
        this.y = cart.getY();
        this.z = cart.getZ();
    }
}