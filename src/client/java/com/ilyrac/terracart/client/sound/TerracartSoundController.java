package com.ilyrac.terracart.client.sound;

import com.ilyrac.terracart.entity.TerracartEntity;
import net.minecraft.client.Minecraft;
import java.util.IdentityHashMap;
import java.util.Map;

public final class TerracartSoundController {

    private static final Map<TerracartEntity, TerracartSoundInstance> ACTIVE_SOUNDS = new IdentityHashMap<>();

    public static void tick(TerracartEntity cart) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        // Check if the player is actually in range of the entity to optimize sound processing
        double distSq = mc.player != null ? cart.distanceToSqr(mc.player) : 0;
        boolean inRange = distSq <= 4096.0; // 64 blocks range

        TerracartSoundInstance sound = ACTIVE_SOUNDS.get(cart);

        // Cart is removed or out of range, stop the sound
        if (cart.isRemoved() || !inRange) {
            if (sound != null) {
                mc.getSoundManager().stop(sound);
                ACTIVE_SOUNDS.remove(cart);
            }
            return;
        }

        // Only start the sound loop if active and fuel exists
        if (sound == null && cart.getEntityData().get(TerracartEntity.SOUND_ACTIVE)) {
            sound = new TerracartSoundInstance(cart);
            ACTIVE_SOUNDS.put(cart, sound);
            mc.getSoundManager().play(sound);
        }
    }
}