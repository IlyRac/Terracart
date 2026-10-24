package com.ilyrac.terracart.loot;

import com.ilyrac.terracart.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

public class ModLootTableModifiers {

    public static void modifyLootTables() {
        StructureCoreCondition.register();

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Village Smithies: 100% guaranteed core per smithy (Weaponsmith, Toolsmith, Armorer)
            // NO village houses or other job chests
            if (BuiltInLootTables.VILLAGE_WEAPONSMITH.equals(key)
                    || BuiltInLootTables.VILLAGE_TOOLSMITH.equals(key)
                    || BuiltInLootTables.VILLAGE_ARMORER.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(1.00F))
                        .add(LootItem.lootTableItem(ModItems.STANDARD_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            }
            // Nether Fortress: Wide-range deck (Chests 2 to 10) + 35% chance for 2nd core (capped at 2)
            else if (BuiltInLootTables.NETHER_BRIDGE.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(StructureCoreCondition.condition(BuiltinStructures.FORTRESS, 2, 10, false))
                        .add(LootItem.lootTableItem(ModItems.STURDY_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            }
            // Bastion Central Treasure: 100% guaranteed core (and grants 2nd core if outer chests already gave 1)
            else if (BuiltInLootTables.BASTION_TREASURE.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(StructureCoreCondition.condition(BuiltinStructures.BASTION_REMNANT, 1, 1, true))
                        .add(LootItem.lootTableItem(ModItems.SWIFT_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            }
            // Bastion Bridge, Hoglin Stable, & Housing (Other): Wide-range deck (Chests 2 to 10) + 35% chance for 2nd core (capped at 2)
            else if (BuiltInLootTables.BASTION_BRIDGE.equals(key)
                    || BuiltInLootTables.BASTION_HOGLIN_STABLE.equals(key)
                    || BuiltInLootTables.BASTION_OTHER.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(StructureCoreCondition.condition(BuiltinStructures.BASTION_REMNANT, 2, 10, false))
                        .add(LootItem.lootTableItem(ModItems.SWIFT_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            }
            // Ancient City: Wide-range deck (Chests 3 to 14) + 35% chance for 2nd core (capped at 2)
            else if (BuiltInLootTables.ANCIENT_CITY.equals(key)
                    || BuiltInLootTables.ANCIENT_CITY_ICE_BOX.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(StructureCoreCondition.condition(BuiltinStructures.ANCIENT_CITY, 3, 14, false))
                        .add(LootItem.lootTableItem(ModItems.PRIME_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            }
        });
    }
}
