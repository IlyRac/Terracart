package com.ilyrac.terracart;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final SoundEvent TERRACART_STANDARD_MOVING = register("terracart_standard_moving");
    public static final SoundEvent TERRACART_SWIFT_MOVING    = register("terracart_swift_moving");
    public static final SoundEvent TERRACART_STURDY_MOVING   = register("terracart_sturdy_moving");
    public static final SoundEvent TERRACART_PRIME_MOVING    = register("terracart_prime_moving");

    public static final SoundEvent TERRACART_REFUEL          = register("terracart_refuel");
    public static final SoundEvent TERRACART_CRASH           = register("terracart_crash");
    public static final SoundEvent TERRACART_HIT             = register("terracart_hit");
    public static final SoundEvent TERRACART_REPAIR          = register("terracart_repair");
    public static final SoundEvent TERRACART_OUT_OF_FUEL     = register("terracart_out_of_fuel");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void initialize() {}
}