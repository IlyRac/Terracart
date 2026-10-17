package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.Terracart;
import com.ilyrac.terracart.tier.TerracartTier;
import net.fabricmc.fabric.api.event.registry.FabricRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    private static EntityType<TerracartEntity> register(String name) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(
                Registries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(Terracart.MOD_ID, name)
        );

        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                key,
                EntityType.Builder
                        .of(TerracartEntity::new, MobCategory.MISC)
                        .sized(3.1F, 1.2F)
                        .clientTrackingRange(8)
                        .updateInterval(1)
                        .nameTagOffset(2.0f)
                        .build(key)
        );
    }

    public static final EntityType<TerracartEntity> STANDARD_TERRACART = register("standard_terracart");
    public static final EntityType<TerracartEntity> SWIFT_TERRACART = register("swift_terracart");
    public static final EntityType<TerracartEntity> STURDY_TERRACART = register("sturdy_terracart");
    public static final EntityType<TerracartEntity> PRIME_TERRACART = register("prime_terracart");

    public static EntityType<TerracartEntity> getEntityTypeFor(TerracartTier tier) {
        return switch (tier) {
            case SWIFT -> SWIFT_TERRACART;
            case STURDY -> STURDY_TERRACART;
            case PRIME -> PRIME_TERRACART;
            case STANDARD -> STANDARD_TERRACART;
        };
    }

    public static void initialize() {
        // Alias legacy entity type 'terracart:terracart' to 'terracart:standard_terracart'
        ((FabricRegistry) BuiltInRegistries.ENTITY_TYPE).addAlias(
                Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "terracart"),
                Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "standard_terracart")
        );
    }
}
