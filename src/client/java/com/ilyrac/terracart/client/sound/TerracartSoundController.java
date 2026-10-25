package com.ilyrac.terracart.client.sound;

import com.ilyrac.terracart.entity.TerracartEntity;
import net.minecraft.client.Minecraft;
import java.util.IdentityHashMap;
import java.util.Map;

public final class TerracartSoundController {

    private static final Map<TerracartEntity, TerracartSoundInstance> ACTIVE_SOUNDS = new IdentityHashMap<>();
    private static final Map<TerracartEntity, TerracartBrakeSoundInstance> ACTIVE_BRAKE_SOUNDS = new IdentityHashMap<>();

    public static void tick(TerracartEntity cart) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        // Check if the player is actually in range of the entity to optimize sound processing
        double distSq = mc.player != null ? cart.distanceToSqr(mc.player) : 0;
        boolean inRange = distSq <= 4096.0; // 64 blocks range

        TerracartSoundInstance sound = ACTIVE_SOUNDS.get(cart);

        // Cart is removed, out of range, or sound stopped (e.g. tier changed), clean up
        if (cart.isRemoved() || !inRange || (sound != null && sound.isStopped())) {
            if (sound != null) {
                mc.getSoundManager().stop(sound);
                ACTIVE_SOUNDS.remove(cart);
                sound = null;
            }
            if (cart.isRemoved() || !inRange) {
                TerracartBrakeSoundInstance brakeSound = ACTIVE_BRAKE_SOUNDS.remove(cart);
                if (brakeSound != null) {
                    mc.getSoundManager().stop(brakeSound);
                }
                return;
            }
        }

        // Only start the sound loop if active and fuel exists
        if (sound == null && cart.isSoundActive()) {
            sound = new TerracartSoundInstance(cart);
            ACTIVE_SOUNDS.put(cart, sound);
            mc.getSoundManager().play(sound);
        }

        // --- BRAKE SOUND CONTROLLER ---
        TerracartBrakeSoundInstance brakeSound = ACTIVE_BRAKE_SOUNDS.get(cart);

        if (brakeSound != null && brakeSound.isStopped()) {
            mc.getSoundManager().stop(brakeSound);
            ACTIVE_BRAKE_SOUNDS.remove(cart);
            brakeSound = null;
        }

        if (brakeSound == null && cart.isBrakingActive()) {
            brakeSound = new TerracartBrakeSoundInstance(cart);
            ACTIVE_BRAKE_SOUNDS.put(cart, brakeSound);
            mc.getSoundManager().play(brakeSound);
        }
    }
}
