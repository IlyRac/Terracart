package com.ilyrac.terracart.loot;

import com.ilyrac.terracart.Terracart;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public record StructureCoreCondition(
        ResourceKey<Structure> structureKey,
        int minRange,
        int maxRange,
        boolean alwaysGuaranteed
) implements LootItemCondition {

    public static final MapCodec<StructureCoreCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            ResourceKey.codec(Registries.STRUCTURE).fieldOf("structure").forGetter(StructureCoreCondition::structureKey),
            Codec.INT.fieldOf("min_range").forGetter(StructureCoreCondition::minRange),
            Codec.INT.fieldOf("max_range").forGetter(StructureCoreCondition::maxRange),
            Codec.BOOL.fieldOf("always_guaranteed").forGetter(StructureCoreCondition::alwaysGuaranteed)
        ).apply(instance, StructureCoreCondition::new)
    );

    public static void register() {
        Registry.register(
            BuiltInRegistries.LOOT_CONDITION_TYPE,
            Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "structure_core"),
            CODEC
        );
    }

    @Override
    public MapCodec<StructureCoreCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(LootContext context) {
        Vec3 origin = context.getOptional(LootContextParams.ORIGIN);
        ServerLevel level = context.getLevel();
        if (origin == null || level == null) {
            return context.getRandom().nextFloat() < (alwaysGuaranteed ? 1.0F : 0.15F);
        }

        BlockPos pos = BlockPos.containing(origin);
        StructureStart start = level.structureManager().getStructureWithPieceAt(pos, holder -> holder.is(this.structureKey));

        if (start == null || !start.isValid()) {
            return context.getRandom().nextFloat() < (alwaysGuaranteed ? 1.0F : 0.15F);
        }

        ChunkPos chunkPos = start.getChunkPos();
        long structureId = chunkPos.pack();

        StructureLootData data = StructureLootData.get(level);
        StructureLootData.Record record = data.getOrCreateRecord(structureId);

        // Cap strictly at 2 cores per structure
        if (record.getCoresAwarded() >= 2) {
            record.incrementChests();
            data.setDirty();
            return false;
        }

        // Special case: Central Treasure Chest in Bastion Treasure room
        if (alwaysGuaranteed) {
            record.incrementCores();
            record.incrementChests();
            data.setDirty();
            return true;
        }

        // Initialize wide-range deck for this structure if not done yet
        if (!record.isInitialized()) {
            record.initDeck(context.getRandom(), minRange, maxRange);
            data.setDirty();
        }

        int currentChest = record.getChestsOpened() + 1; // 1-indexed chest number
        record.incrementChests();

        boolean dropsCore = false;

        // Hit either the first target or second target in the deck
        if (currentChest == record.getFirstTarget() || currentChest == record.getSecondTarget()) {
            dropsCore = true;
        } else if (currentChest >= maxRange && record.getCoresAwarded() == 0) {
            // Safety net: reached end of range and no core yet -> guarantee!
            dropsCore = true;
        }

        if (dropsCore) {
            record.incrementCores();
            data.setDirty();
            return true;
        }

        data.setDirty();
        return false;
    }

    public static LootItemCondition.Builder condition(ResourceKey<Structure> structureKey, int minRange, int maxRange, boolean alwaysGuaranteed) {
        return () -> new StructureCoreCondition(structureKey, minRange, maxRange, alwaysGuaranteed);
    }
}
