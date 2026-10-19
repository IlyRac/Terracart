package com.ilyrac.terracart.tier;

import com.ilyrac.terracart.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum TerracartTier implements StringRepresentable {
    STANDARD("standard", "Standard", 19, 21, 90, 110, 90, 110),
    SWIFT("swift", "Swift", 23, 25, 70, 90, 70, 90),
    STURDY("sturdy", "Sturdy", 17, 19, 120, 140, 120, 140),
    PRIME("prime", "Prime", 21, 23, 110, 120, 110, 120);

    private final String id;
    private final String displayName;
    private final int minSpeed;
    private final int maxSpeed;
    private final int minFuel;
    private final int maxFuel;
    private final int minCondition;
    private final int maxCondition;

    TerracartTier(String id, String displayName, int minSpeed, int maxSpeed, int minFuel, int maxFuel, int minCondition, int maxCondition) {
        this.id = id;
        this.displayName = displayName;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.minFuel = minFuel;
        this.maxFuel = maxFuel;
        this.minCondition = minCondition;
        this.maxCondition = maxCondition;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getMinFuel() {
        return minFuel;
    }

    public int getMaxFuel() {
        return maxFuel;
    }

    public int getMinCondition() {
        return minCondition;
    }

    public int getMaxCondition() {
        return maxCondition;
    }

    public SoundEvent getMovingSound() {
        return switch (this) {
            case STANDARD -> ModSounds.TERRACART_STANDARD_MOVING;
            case SWIFT -> ModSounds.TERRACART_SWIFT_MOVING;
            case STURDY -> ModSounds.TERRACART_STURDY_MOVING;
            case PRIME -> ModSounds.TERRACART_PRIME_MOVING;
        };
    }

    @Override
    public @NonNull String getSerializedName() {
        return id;
    }
}
