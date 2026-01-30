package com.ilyrac.terracart.sound;

import com.ilyrac.terracart.entity.TerracartEntity;
import net.minecraft.client.Minecraft;
import java.util.IdentityHashMap;
import java.util.Map;

public final class TerracartSoundController {

    private static final Map<TerracartEntity, TerracartSoundInstance> ACTIVE_SOUNDS = new IdentityHashMap<>();

    public static void tick(TerracartEntity cart) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        TerracartSoundInstance sound = ACTIVE_SOUNDS.get(cart);

        // cart is removed, stop the sound instance
        if (cart.isRemoved()) {
            if (sound != null) {
                mc.getSoundManager().stop(sound);
                ACTIVE_SOUNDS.remove(cart);
            }
            return;
        }

        if (sound == null) {
            sound = new TerracartSoundInstance(cart);
            ACTIVE_SOUNDS.put(cart, sound);
            mc.getSoundManager().play(sound);
        }
    }
}
