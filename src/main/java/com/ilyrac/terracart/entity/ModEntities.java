package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.Terracart;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static final ResourceKey<EntityType<?>> TERRACART_KEY =
            ResourceKey.create(
                    Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "terracart")
            );

    public static final EntityType<TerracartEntity> TERRACART =
            Registry.register(
                    BuiltInRegistries.ENTITY_TYPE,
                    TERRACART_KEY,
                    EntityType.Builder
                            .of(TerracartEntity::new, MobCategory.MISC)
                            .sized(1.6F, 1.1F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TERRACART_KEY)
            );

    public static void initialize() {}
}
