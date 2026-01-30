package com.ilyrac.terracart;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Registry;

public class ModSounds {

    public static final SoundEvent TERRACART_SOUND = registerSound();

    private static SoundEvent registerSound() {
        Identifier identifier = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "terracart_sound");
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    public static void initialize() {}
}