package com.ilyrac.terracart.component;

import com.ilyrac.terracart.Terracart;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModDataComponents {

    public static final DataComponentType<TerracartCoreStats> CORE_STATS = register(
            DataComponentType.<TerracartCoreStats>builder()
                    .persistent(TerracartCoreStats.CODEC)
                    .networkSynchronized(TerracartCoreStats.STREAM_CODEC)
                    .build()
    );

    private static <T> DataComponentType<T> register(DataComponentType<T> type) {
        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "core_stats"),
                type
        );
    }

    public static void initialize() {}
}