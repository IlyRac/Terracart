package com.ilyrac.terracart.loot;

import com.ilyrac.terracart.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

public class ModLootTableModifiers {

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (BuiltInLootTables.VILLAGE_WEAPONSMITH.equals(key)
                    || BuiltInLootTables.VILLAGE_TOOLSMITH.equals(key)
                    || BuiltInLootTables.VILLAGE_ARMORER.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.50F))
                        .add(LootItem.lootTableItem(ModItems.STANDARD_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            } else if (BuiltInLootTables.NETHER_BRIDGE.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.15F))
                        .add(LootItem.lootTableItem(ModItems.STURDY_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            } else if (BuiltInLootTables.BASTION_TREASURE.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.75F))
                        .add(LootItem.lootTableItem(ModItems.SWIFT_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            } else if (BuiltInLootTables.BASTION_BRIDGE.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.20F))
                        .add(LootItem.lootTableItem(ModItems.SWIFT_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            } else if (BuiltInLootTables.BASTION_HOGLIN_STABLE.equals(key)
                    || BuiltInLootTables.BASTION_OTHER.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.10F))
                        .add(LootItem.lootTableItem(ModItems.SWIFT_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            } else if (BuiltInLootTables.ANCIENT_CITY.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.05F))
                        .add(LootItem.lootTableItem(ModItems.PRIME_TERRACART_CORE));
                tableBuilder.pool(pool.build());
            }
        });
    }
}
