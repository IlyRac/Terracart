package com.ilyrac.terracart.loot;

import com.ilyrac.terracart.Terracart;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;

public class StructureLootData extends SavedData {

    public static class Record {
        private int coresAwarded;
        private int chestsOpened;
        private int firstTarget;
        private int secondTarget; // -1 if no second core rolled
        private boolean initialized;

        public static final Codec<Record> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("cores").forGetter(r -> r.coresAwarded),
                Codec.INT.fieldOf("chests").forGetter(r -> r.chestsOpened),
                Codec.INT.fieldOf("first_target").forGetter(r -> r.firstTarget),
                Codec.INT.fieldOf("second_target").forGetter(r -> r.secondTarget),
                Codec.BOOL.fieldOf("initialized").forGetter(r -> r.initialized)
            ).apply(instance, Record::new)
        );

        public Record() {
            this(0, 0, -1, -1, false);
        }

        public Record(int coresAwarded, int chestsOpened, int firstTarget, int secondTarget, boolean initialized) {
            this.coresAwarded = coresAwarded;
            this.chestsOpened = chestsOpened;
            this.firstTarget = firstTarget;
            this.secondTarget = secondTarget;
            this.initialized = initialized;
        }

        /**
         * Initializes a shuffled deck for this structure:
         * - Core #1 is placed randomly between minRange and maxRange (1-indexed).
         * - Core #2 has a 35% chance to exist and is placed in another random chest.
         */
        public void initDeck(RandomSource random, int minRange, int maxRange) {
            if (this.initialized) return;

            int span = Math.max(1, maxRange - minRange + 1);
            this.firstTarget = minRange + random.nextInt(span);

            // 35% chance that a 2nd bonus core is shuffled into this structure
            if (random.nextFloat() < 0.35F) {
                int second;
                int attempts = 0;
                do {
                    second = 1 + random.nextInt(maxRange + 4);
                    attempts++;
                } while (second == this.firstTarget && attempts < 10);
                this.secondTarget = second;
            } else {
                this.secondTarget = -1;
            }

            this.initialized = true;
        }

        public int getCoresAwarded() { return coresAwarded; }
        public int getChestsOpened() { return chestsOpened; }
        public int getFirstTarget() { return firstTarget; }
        public int getSecondTarget() { return secondTarget; }
        public boolean isInitialized() { return initialized; }

        public void incrementCores() { this.coresAwarded++; }
        public void incrementChests() { this.chestsOpened++; }
    }

    private final Map<Long, Record> records = new HashMap<>();

    public static final Codec<StructureLootData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.unboundedMap(Codec.STRING, Record.CODEC)
                .xmap(
                    map -> {
                        Map<Long, Record> res = new HashMap<>();
                        map.forEach((k, v) -> {
                            try {
                                res.put(Long.parseLong(k), v);
                            } catch (NumberFormatException ignored) {}
                        });
                        return res;
                    },
                    map -> {
                        Map<String, Record> res = new HashMap<>();
                        map.forEach((k, v) -> res.put(k.toString(), v));
                        return res;
                    }
                )
                .fieldOf("records")
                .forGetter(data -> data.records)
        ).apply(instance, StructureLootData::new)
    );

    public static final SavedDataType<StructureLootData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "structure_loot"),
        StructureLootData::new,
        CODEC,
        DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    public StructureLootData() {
    }

    public StructureLootData(Map<Long, Record> initial) {
        this.records.putAll(initial);
    }

    public static StructureLootData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public Record getOrCreateRecord(long structureId) {
        return this.records.computeIfAbsent(structureId, k -> new Record());
    }
}
