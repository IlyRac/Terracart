package com.ilyrac.terracart.sound;

import com.ilyrac.terracart.entity.TerraCartEntity;
import net.minecraft.client.Minecraft;
import java.util.IdentityHashMap;
import java.util.Map;

public final class TerraCartSoundController {

    private static final Map<TerraCartEntity, TerraCartSoundInstance> ACTIVE_SOUNDS = new IdentityHashMap<>();

    public static void tick(TerraCartEntity cart) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        TerraCartSoundInstance sound = ACTIVE_SOUNDS.get(cart);

        // cart is removed, stop the sound instance
        if (cart.isRemoved()) {
            if (sound != null) {
                mc.getSoundManager().stop(sound);
                ACTIVE_SOUNDS.remove(cart);
            }
            return;
        }

        if (sound == null) {
            sound = new TerraCartSoundInstance(cart);
            ACTIVE_SOUNDS.put(cart, sound);
            mc.getSoundManager().play(sound);
        }
    }
}
