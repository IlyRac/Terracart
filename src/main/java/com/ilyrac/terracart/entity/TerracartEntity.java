package com.ilyrac.terracart.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TerracartEntity extends VehicleEntity {

    // ================================================================================================================
    //    DATA MANAGEMENT (STATE, SYNCS, GETTERS & SETTERS)
    // ================================================================================================================

    // --- 1. HEALTH SYSTEM ---
    private static final EntityDataAccessor<Float> CURRENT_HEALTH = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);

    public static final float MAX_HEALTH = 100.0f;
    public float getHealth() { return this.entityData.get(CURRENT_HEALTH); }
    public void setHealth(float hp) { this.entityData.set(CURRENT_HEALTH, Mth.clamp(hp, 0.0f, MAX_HEALTH)); }
    public float getHealthPercent() { return this.getHealth() / MAX_HEALTH; }

    // --- 2. FUEL SYSTEM ---
    public static final int MAX_FUEL = 24000;
    private static final EntityDataAccessor<Integer> FUEL_TICKS = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);

    public int getFuel() { return this.entityData.get(FUEL_TICKS); }
    public void setFuel(int ticks) { this.entityData.set(FUEL_TICKS, ticks); }
    public boolean hasFuel() { return this.getFuel() > 0; }
    public float getFuelPercent() { return (float) this.getFuel() / (float) MAX_FUEL; }

    // --- 3. DRIVER INPUTS & LOCAL PHYSICS TRACKERS ---
    private float driverForward = 0.0f;
    private float driverStrafe = 0.0f;
    private double currentSpeed = 0.0;
    private float speedBps = 0.0F;
    private Vec3 lastPos = Vec3.ZERO;
    private double lastX; // <-- Added back
    private double lastZ; // <-- Added back
    private boolean wasAirborne = false;
    private double airborneStartY = 0.0;

    public void setDriverInput(float fwd, float str) {
        this.driverForward = Mth.clamp(fwd, -1.0f, 1.0f);
        this.driverStrafe = Mth.clamp(str, -1.0f, 1.0f);
    }
    public float getDriverForward() { return driverForward; }
    public float getDriverStrafe() { return driverStrafe; }

    public double getCurrentSpeed() { return currentSpeed; }
    public void setCurrentSpeed(double speed) { this.currentSpeed = speed; }
    public float getSpeedBps() { return speedBps; }

    public boolean wasAirborne() { return wasAirborne; }
    public void setWasAirborne(boolean airborne) { this.wasAirborne = airborne; }
    public double getAirborneStartY() { return airborneStartY; }
    public void setAirborneStartY(double y) { this.airborneStartY = y; }

    // --- 4. ENGINE AUDIO STATES ---
    public static final EntityDataAccessor<Boolean> SOUND_ACTIVE = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> SOUND_VOLUME = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SOUND_PITCH = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);

    public void setSoundActive(boolean active) { this.entityData.set(SOUND_ACTIVE, active); }
    public float getSoundVolume() { return this.entityData.get(SOUND_VOLUME); }
    public void setSoundVolume(float volume) { this.entityData.set(SOUND_VOLUME, volume); }
    public float getSoundPitch() { return this.entityData.get(SOUND_PITCH); }
    public void setSoundPitch(float pitch) { this.entityData.set(SOUND_PITCH, pitch); }

    // --- 5. WHEELS & COSMETICS ---
    private static final EntityDataAccessor<Integer> CART_COLOR = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> WHEEL_ROTATION = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);
    private float prevWheelRotation = 0.0f;

    public void setCartColor(int color) { this.entityData.set(CART_COLOR, color < 0 ? -1 : Math.min(15, color)); }
    public int getCartColor() { return this.entityData.get(CART_COLOR); }

    public float getWheelRotation() { return this.entityData.get(WHEEL_ROTATION); }
    public float getPrevWheelRotation() { return this.prevWheelRotation; }

    // --- 6. ACTION & RISK COOLDOWNS ---
    private int hitCooldown = 0;
    private int fireCooldown = 0;
    private int MovingSoundCooldown = 0;

    public int getHitCooldown() { return hitCooldown; }
    public void setHitCooldown(int cooldown) { this.hitCooldown = cooldown; }
    public int getFireCooldown() { return fireCooldown; }
    public void setFireCooldown(int cooldown) { this.fireCooldown = cooldown; }

    public TerracartEntity(EntityType<? extends TerracartEntity> type, Level level) {
        super(type, level);
        this.lastX = this.getX(); this.lastZ = this.getZ();
        this.yRotO = this.getYRot(); this.xRotO = this.getXRot();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CART_COLOR, -1).define(FUEL_TICKS, 1200).define(WHEEL_ROTATION, 0.0f)
                .define(SOUND_ACTIVE, false).define(SOUND_VOLUME, 1.0f).define(SOUND_PITCH, 1.0f)
                .define(CURRENT_HEALTH, MAX_HEALTH);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput out) {
        out.putInt("CartColor", getCartColor()); out.putInt("FuelTicks", getFuel()); out.putFloat("Health", getHealth());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput in) {
        setCartColor(in.getIntOr("CartColor", -1)); setFuel(in.getIntOr("FuelTicks", 0)); setHealth(in.getFloatOr("Health", MAX_HEALTH));
    }

    // ================================================================================================================
    //    MAIN TICK LOOP
    // ================================================================================================================

    @Override
    public void tick() {
        super.tick();
        this.prevWheelRotation = this.getWheelRotation();

        if (lastPos != Vec3.ZERO) speedBps = (float) (this.position().distanceTo(lastPos) * 20.0);
        lastPos = this.position();

        TerracartStateManager.burnFuel(this);
        if (MovingSoundCooldown > 0) MovingSoundCooldown--;
        if (hitCooldown > 0) hitCooldown--;
        if (fireCooldown > 0) fireCooldown--;

        TerracartStateManager.handleHazards(this);

        // Movement & Physics
        Vec3 motion = TerracartPhysics.applyControllerInput(this, TerracartPhysics.applyGravity(this, this.getDeltaMovement()));
        this.setDeltaMovement(motion);
        this.move(MoverType.SELF, motion);

        TerracartPhysics.handleStepUp(this, motion);
        TerracartPhysics.handleLandFallDamage(this);

        // Visuals & Rotations
        double dx = this.getX() - lastX, dz = this.getZ() - lastZ;
        double speedSq = Math.sqrt(dx * dx + dz * dz);
        TerracartStateManager.spawnMovementParticles(this, dx, dz, speedSq);
        TerracartStateManager.updateSoundState(this, speedSq);

        float yawRad = (float) Math.toRadians(this.getYRot());
        double signedDistance = dx * (-Math.sin(yawRad)) + dz * Math.cos(yawRad);
        this.entityData.set(WHEEL_ROTATION, this.prevWheelRotation + (float) (-signedDistance / 0.35f));

        TerracartCollisionHandler.handleCollisions(this, speedSq);

        lastX = this.getX(); lastZ = this.getZ();
    }

    // ================================================================================================================
    //    DELEGATED ACTIONS & COMPATIBILITY OVERRIDES
    // ================================================================================================================

    @Override
    public @NonNull InteractionResult interact(@NonNull Player player, @NonNull InteractionHand hand, @NonNull Vec3 pos) {
        return TerracartInteractionHandler.handleInteraction(this, player, hand);
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel sl, @NotNull DamageSource src, float amount) {
        if (this.isInvulnerableToBase(src)) {
            return false;
        }
        return TerracartInteractionHandler.handleHurtServer(this, sl, amount);
    }

    @Override
    public void push(@NonNull Entity entity) {
        TerracartCollisionHandler.performPush(this, entity);
    }

    @Override
    protected void destroy(@NonNull ServerLevel sl, @NonNull DamageSource src) {
        TerracartInteractionHandler.handleDestroy(this, sl, src);
    }

    @Override
    public @NotNull ItemStack getPickResult() {
        return TerracartInteractionHandler.handlePickResult(this);
    }

    @Override
    protected boolean canAddPassenger(@NonNull Entity passenger) {
        return TerracartCompatibilityHandler.canAddPassenger(this, passenger);
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return TerracartCompatibilityHandler.getControllingPassenger(this);
    }

    @Override
    public @NonNull Vec3 getPassengerRidingPosition(@NonNull Entity passenger) {
        return TerracartCompatibilityHandler.getPassengerRidingPosition(this, passenger);
    }

    @Override
    public boolean canRide(@NonNull Entity entity) {
        return TerracartCompatibilityHandler.canRide(this, entity);
    }

    @Override
    protected @NonNull Item getDropItem() {
        return TerracartCompatibilityHandler.getDropItem();
    }

    @Override
    public boolean isPickable() {
        return TerracartCompatibilityHandler.isPickable(this);
    }

    @Override
    public boolean isPushable() {
        return TerracartCompatibilityHandler.isPushable(this);
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return TerracartCompatibilityHandler.canBeCollidedWith(this, entity);
    }

    @Override
    public boolean canCollideWith(@Nullable Entity entity) {
        return TerracartCompatibilityHandler.canCollideWith(this, entity);
    }

    @Override
    public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
        return TerracartCompatibilityHandler.getDimensions(pose);
    }

    @Override
    protected void playStepSound(@NonNull BlockPos pos, @NonNull BlockState state) {}

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NonNull BlockState state, @NonNull BlockPos pos) {}

    @Override
    public boolean hasCustomName() {
        return this.getCustomName() != null;
    }

    @Override
    public boolean shouldShowName() {
        return this.hasCustomName();
    }
}