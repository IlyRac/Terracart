package com.ilyrac.terracart;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Registry;

public class ModSounds {

    public static final SoundEvent TERRACART_MOVING_SOUND  = register("terracart_moving_sound");
    public static final SoundEvent TERRACART_REFUEL = register("terracart_refuel");
    public static final SoundEvent TERRACART_CRASH = register("terracart_crash");
    public static final SoundEvent TERRACART_HIT = register("terracart_hit");
    public static final SoundEvent TERRACART_REPAIR = register("terracart_repair");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void initialize() {}
}