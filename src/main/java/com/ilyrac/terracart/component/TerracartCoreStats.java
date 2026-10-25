package com.ilyrac.terracart.component;

import com.ilyrac.terracart.tier.TerracartTier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

public record TerracartCoreStats(float maxSpeed, int maxFuel, int maxCondition) {

    public static final Codec<TerracartCoreStats> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("max_speed").forGetter(TerracartCoreStats::maxSpeed),
                    Codec.INT.fieldOf("max_fuel").forGetter(TerracartCoreStats::maxFuel),
                    Codec.INT.fieldOf("max_condition").forGetter(TerracartCoreStats::maxCondition)
            ).apply(instance, TerracartCoreStats::new)
    );

    public static final StreamCodec<ByteBuf, TerracartCoreStats> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, TerracartCoreStats::maxSpeed,
            ByteBufCodecs.VAR_INT, TerracartCoreStats::maxFuel,
            ByteBufCodecs.VAR_INT, TerracartCoreStats::maxCondition,
            TerracartCoreStats::new
    );

    public static TerracartCoreStats forMaxTier(TerracartTier tier) {
        return new TerracartCoreStats((float) tier.getMaxSpeed(), tier.getMaxFuel(), tier.getMaxCondition());
    }

    public static TerracartCoreStats forRandomTier(TerracartTier tier, RandomSource random) {
        float speed = Math.round((tier.getMinSpeed() + random.nextFloat() * (tier.getMaxSpeed() - tier.getMinSpeed())) * 10.0f) / 10.0f;
        int fuel = random.nextIntBetweenInclusive(tier.getMinFuel(), tier.getMaxFuel());
        int condition = random.nextIntBetweenInclusive(tier.getMinCondition(), tier.getMaxCondition());
        return new TerracartCoreStats(speed, fuel, condition);
    }

    public static TerracartCoreStats forDefault(TerracartTier tier) {
        float speed = (tier.getMinSpeed() + tier.getMaxSpeed()) / 2.0f;
        int fuel = (tier.getMinFuel() + tier.getMaxFuel()) / 2;
        int condition = (tier.getMinCondition() + tier.getMaxCondition()) / 2;
        return new TerracartCoreStats(speed, fuel, condition);
    }
}
