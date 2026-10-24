package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import com.ilyrac.terracart.tier.TerracartTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
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
import net.minecraft.world.level.block.LeavesBlock;
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

    // --- 0. ENGINE CORE STATS ---
    private static final EntityDataAccessor<Float> CORE_MAX_SPEED = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CORE_MAX_FUEL = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CORE_MAX_CONDITION = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);

    public com.ilyrac.terracart.component.TerracartCoreStats getCoreStats() {
        return new com.ilyrac.terracart.component.TerracartCoreStats(
                this.entityData.get(CORE_MAX_SPEED),
                this.entityData.get(CORE_MAX_FUEL),
                this.entityData.get(CORE_MAX_CONDITION)
        );
    }

    public void setCoreStats(com.ilyrac.terracart.component.TerracartCoreStats stats) {
        this.entityData.set(CORE_MAX_SPEED, stats.maxSpeed());
        this.entityData.set(CORE_MAX_FUEL, stats.maxFuel());
        this.entityData.set(CORE_MAX_CONDITION, stats.maxCondition());
    }

    public static final int TICKS_PER_FUEL_UNIT = 160; // 1 FU = 160 ticks = 8.0s. 15 FU (1 Coal) = 2,400 ticks = 2.0 minutes.

    public float getMaxHealth() {
        int condition = this.entityData.get(CORE_MAX_CONDITION);
        return condition > 0 ? (float) condition : 100.0f;
    }

    public int getMaxFuel() {
        int fuelStat = this.entityData.get(CORE_MAX_FUEL);
        return fuelStat > 0 ? fuelStat * TICKS_PER_FUEL_UNIT : 100 * TICKS_PER_FUEL_UNIT;
    }

    // --- 1. HEALTH SYSTEM ---
    private static final EntityDataAccessor<Float> CURRENT_HEALTH = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);

    public float getHealth() { return this.entityData.get(CURRENT_HEALTH); }
    public void setHealth(float hp) { this.entityData.set(CURRENT_HEALTH, Mth.clamp(hp, 0.0f, this.getMaxHealth())); }
    public float getHealthPercent() { return this.getHealth() / this.getMaxHealth(); }

    // --- 2. FUEL SYSTEM ---
    private static final EntityDataAccessor<Integer> FUEL_TICKS = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);
    private int localFuel = 100 * TICKS_PER_FUEL_UNIT;

    public int getFuel() { return this.localFuel; }
    public void setFuel(int ticks) {
        this.localFuel = Math.min(ticks, this.getMaxFuel());
        this.entityData.set(FUEL_TICKS, this.localFuel);
    }
    public void setFuelLocal(int ticks) {
        this.localFuel = Math.min(ticks, this.getMaxFuel());
    }
    public void syncFuel() {
        this.entityData.set(FUEL_TICKS, this.localFuel);
    }
    public boolean hasFuel() { return this.getFuel() > 0; }
    public float getFuelPercent() { return (float) this.getFuel() / (float) this.getMaxFuel(); }

    // --- 3. DRIVER INPUTS & LOCAL PHYSICS TRACKERS ---
    private double currentSpeed = 0.0;
    private float speedBps = 0.0F;
    private Vec3 lastPos = Vec3.ZERO;
    private double lastX;
    private double lastZ;
    private boolean wasAirborne = false;
    private double airborneStartY = 0.0;
    private boolean isFirstTick = true;

    private float prevForwardInput = 0.0f;

    public void setDriverInput(float fwd, float str) {
        float clampedFwd = Mth.clamp(fwd, -1.0f, 1.0f);
        if (Math.abs(clampedFwd) > 0.1f && Math.abs(this.prevForwardInput) <= 0.1f && !this.level().isClientSide()) {
            boolean inDeepWater = this.getFluidHeight(FluidTags.WATER) > 1.05;
            if (inDeepWater) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.TERRACART_ENGINE_SPUTTERS, SoundSource.NEUTRAL, 0.9f, 0.85f);
                if (this.level() instanceof ServerLevel sl) {
                    TerracartStateManager.spawnPerimeterParticles(sl, this, ParticleTypes.BUBBLE, 24, 0.05);
                    TerracartStateManager.spawnPerimeterParticles(sl, this, ParticleTypes.BUBBLE_POP, 12, 0.04);
                }
            } else if (!this.hasFuel()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.TERRACART_ENGINE_SPUTTERS, SoundSource.NEUTRAL, 0.8f, 1.0f);
                if (this.level() instanceof ServerLevel sl) {
                    TerracartStateManager.spawnPerimeterParticles(sl, this, new DustParticleOptions(0x111111, 1.2f), 28, 0.03);
                }
            }
        }
        this.prevForwardInput = clampedFwd;
        this.entityData.set(DRIVER_FORWARD, clampedFwd);
        this.entityData.set(WHEEL_TURNING, Mth.clamp(str, -1.0f, 1.0f));
    }

    public double getCurrentSpeed() { return currentSpeed; }
    public void setCurrentSpeed(double speed) { this.currentSpeed = speed; }
    public float getSpeedBps() { return speedBps; }

    public boolean wasAirborne() { return wasAirborne; }
    public void setWasAirborne(boolean airborne) { this.wasAirborne = airborne; }
    public double getAirborneStartY() { return airborneStartY; }
    public void setAirborneStartY(double y) { this.airborneStartY = y; }

    // --- 3b. DIRECTIONAL SHIFTING (BRAKING & REVERSING) ---
    private int currentGear = 1; // 1 = forward (Drive), -1 = reverse
    private int shiftDelayTimer = 0;
    private int shiftTargetDirection = 0; // 1 = forward, -1 = reverse

    public int getCurrentGear() { return currentGear; }
    public void setCurrentGear(int gear) { this.currentGear = gear; }
    public int getShiftDelayTimer() { return shiftDelayTimer; }
    public void setShiftDelayTimer(int timer) { this.shiftDelayTimer = timer; }
    public int getShiftTargetDirection() { return shiftTargetDirection; }
    public void setShiftTargetDirection(int dir) { this.shiftTargetDirection = dir; }

    // --- 3c. IMPACT STUN (CRASH & FALL RECOVERY) ---
    public static final EntityDataAccessor<Integer> STUN_TICKS = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);

    public int getStunTicks() { return this.entityData.get(STUN_TICKS); }
    public void setStunTicks(int ticks) { this.entityData.set(STUN_TICKS, ticks); }

    // --- 3d. SLOPE TRAVERSAL (UPHILL RESISTANCE & DOWNHILL GRAVITY) ---
    private int inclineTimer = 0;
    private int descentTimer = 0;
    private int stepBleedCooldown = 0;

    public int getInclineTimer() { return inclineTimer; }
    public void setInclineTimer(int timer) { this.inclineTimer = timer; }
    public int getDescentTimer() { return descentTimer; }
    public void setDescentTimer(int timer) { this.descentTimer = timer; }
    public int getStepBleedCooldown() { return stepBleedCooldown; }
    public void setStepBleedCooldown(int ticks) { this.stepBleedCooldown = ticks; }

    // --- 3e. FOLIAGE & LEAF INTERACTION ---
    private int leafSoundCooldown = 0;

    public int getLeafSoundCooldown() { return leafSoundCooldown; }
    public void setLeafSoundCooldown(int ticks) { this.leafSoundCooldown = ticks; }

    // --- 3f. BRAKING STATE ---
    public static final EntityDataAccessor<Boolean> BRAKING_ACTIVE = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.BOOLEAN);

    public boolean isBrakingActive() { return this.entityData.get(BRAKING_ACTIVE); }
    public void setBrakingActive(boolean active) { this.entityData.set(BRAKING_ACTIVE, active); }

    public boolean isInLeaves() {
        return this.level().getBlockStates(this.getBoundingBox()).anyMatch(state -> state.getBlock() instanceof LeavesBlock);
    }


    // --- 4. AUDIO & COSMETICS ---
    public static final EntityDataAccessor<Boolean> SOUND_ACTIVE = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CART_COLOR = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> WHEEL_TURNING = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DRIVER_FORWARD = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);

    // Client-side local wheel rotation (zero network packets)
    private float wheelRotation = 0.0f;
    private float prevWheelRotation = 0.0f;

    public void setCartColor(int color) { this.entityData.set(CART_COLOR, color < 0 ? -1 : Math.min(15, color)); }
    public int getCartColor() { return this.entityData.get(CART_COLOR); }
    public float getWheelRotation() { return this.wheelRotation; }
    public float getPrevWheelRotation() { return this.prevWheelRotation; }
    public void setSoundActive(boolean active) { this.entityData.set(SOUND_ACTIVE, active); }
    public boolean isSoundActive() { return this.entityData.get(SOUND_ACTIVE); }

    public TerracartTier getTier() {
        if (this.getType() == ModEntities.SWIFT_TERRACART) return TerracartTier.SWIFT;
        if (this.getType() == ModEntities.STURDY_TERRACART) return TerracartTier.STURDY;
        if (this.getType() == ModEntities.PRIME_TERRACART) return TerracartTier.PRIME;
        return TerracartTier.STANDARD;
    }

    // --- 6. ACTION & RISK COOLDOWNS ---
    private int hitCooldown = 0;
    private int mobHitCooldown = 0;
    private int cartCollisionCooldown = 0;
    private int fireCooldown = 0;

    public int getHitCooldown() { return hitCooldown; }
    public void setHitCooldown(int cooldown) { this.hitCooldown = cooldown; }
    public int getMobHitCooldown() { return mobHitCooldown; }
    public void setMobHitCooldown(int cooldown) { this.mobHitCooldown = cooldown; }
    public int getCartCollisionCooldown() { return cartCollisionCooldown; }
    public void setCartCollisionCooldown(int cooldown) { this.cartCollisionCooldown = cooldown; }
    public int getFireCooldown() { return fireCooldown; }
    public void setFireCooldown(int cooldown) { this.fireCooldown = cooldown; }
    public float getSyncedForward() { return this.entityData.get(DRIVER_FORWARD); }

    public TerracartEntity(EntityType<? extends TerracartEntity> type, Level level) {
        super(type, level);
        this.lastX = this.getX(); this.lastZ = this.getZ();
        this.yRotO = this.getYRot(); this.xRotO = this.getXRot();
        TerracartTier tier = this.getTier();
        this.setCoreStats(com.ilyrac.terracart.component.TerracartCoreStats.forRandomTier(tier, this.random));
        this.setHealth(this.getMaxHealth());
        int startFuel = 5 + this.random.nextInt(11); // 5 to 15 FU
        this.setFuel(startFuel * TICKS_PER_FUEL_UNIT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CORE_MAX_SPEED, 20.0f)
                .define(CORE_MAX_FUEL, 100)
                .define(CORE_MAX_CONDITION, 100)
                .define(CART_COLOR, -1)
                .define(FUEL_TICKS, 100 * TICKS_PER_FUEL_UNIT)
                .define(SOUND_ACTIVE, false)
                .define(CURRENT_HEALTH, 100.0f)
                .define(WHEEL_TURNING, 0.0f)
                .define(DRIVER_FORWARD, 0.0f)
                .define(STUN_TICKS, 0)
                .define(BRAKING_ACTIVE, false);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput out) {
        out.putInt("CartColor", getCartColor());
        out.putInt("FuelTicks", getFuel());
        out.putFloat("Health", getHealth());
        out.putFloat("CoreSpeed", this.entityData.get(CORE_MAX_SPEED));
        out.putInt("CoreFuel", this.entityData.get(CORE_MAX_FUEL));
        out.putInt("CoreCondition", this.entityData.get(CORE_MAX_CONDITION));
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput in) {
        setCartColor(in.getIntOr("CartColor", -1));
        float coreSpeed = in.getFloatOr("CoreSpeed", 0.0f);
        if (coreSpeed > 0.0f) {
            setCoreStats(new com.ilyrac.terracart.component.TerracartCoreStats(
                    coreSpeed,
                    in.getIntOr("CoreFuel", 100),
                    in.getIntOr("CoreCondition", 100)
            ));
        } else {
            // Legacy cart or summon without stats: roll random stats within tier range!
            setCoreStats(com.ilyrac.terracart.component.TerracartCoreStats.forRandomTier(this.getTier(), this.random));
        }
        int savedFuel = in.getIntOr("FuelTicks", this.getMaxFuel());
        this.localFuel = savedFuel;
        this.entityData.set(FUEL_TICKS, savedFuel);
        setHealth(in.getFloatOr("Health", this.getMaxHealth()));
    }

    @Override
    public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (FUEL_TICKS.equals(key)) {
            this.localFuel = this.entityData.get(FUEL_TICKS);
        }
    }

    public float getSyncedStrafe() {
        return this.entityData.get(WHEEL_TURNING);
    }

    // ================================================================================================================
    //    MAIN TICK LOOP
    // ================================================================================================================

    @Override
    public void tick() {
        // 1. CRITICAL RENDER FIX: Cache history BEFORE any movement.
        // This stops the passenger from visually floating behind the cart.
        this.setOldPosAndRot();

        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.prevWheelRotation = this.getWheelRotation();

        // Prevent teleport/spawn physics leap on first tick
        if (isFirstTick) {
            this.lastX = this.getX();
            this.lastZ = this.getZ();
            this.lastPos = this.position();
            this.isFirstTick = false;
        }

        if (lastPos != Vec3.ZERO) {
            double hdx = this.getX() - lastPos.x;
            double hdz = this.getZ() - lastPos.z;
            speedBps = (float) (Math.sqrt(hdx * hdx + hdz * hdz) * 20.0);
        }
        lastPos = this.position();

        TerracartStateManager.burnFuel(this);
        if (hitCooldown > 0) hitCooldown--;
        if (mobHitCooldown > 0) mobHitCooldown--;
        if (cartCollisionCooldown > 0) cartCollisionCooldown--;
        if (fireCooldown > 0) fireCooldown--;
        int st = getStunTicks();
        if (st > 0) setStunTicks(st - 1);
        if (inclineTimer > 0) inclineTimer--;
        if (descentTimer > 0) descentTimer--;
        if (stepBleedCooldown > 0) stepBleedCooldown--;
        if (leafSoundCooldown > 0) leafSoundCooldown--;

        TerracartStateManager.handleHazards(this);

        // 2. Execute Universal Physics
        Vec3 motion = TerracartPhysics.applyControllerInput(this, TerracartPhysics.applyGravity(this, this.getDeltaMovement()));
        this.setDeltaMovement(motion);
        this.move(MoverType.SELF, motion);

        boolean steppedUp = TerracartPhysics.handleStepUp(this, motion);
        if (!steppedUp) {
            TerracartCollisionHandler.checkWallCrash(this, motion);
            TerracartPhysics.handleStepDown(this, motion);
        }
        TerracartPhysics.handleLandFallDamage(this);

        // Visuals & Rotations
        double dx = this.getX() - lastX, dz = this.getZ() - lastZ;
        double speedSq = Math.sqrt(dx * dx + dz * dz);
        TerracartStateManager.spawnMovementParticles(this, dx, dz, speedSq);
        TerracartStateManager.updateSoundState(this, speedSq);

        float yawRad = (float) Math.toRadians(this.getYRot());
        double signedDistance = dx * (-Math.sin(yawRad)) + dz * Math.cos(yawRad);
        this.wheelRotation = this.prevWheelRotation + (float) (-signedDistance / 0.35f);

        this.setXRot(0.0F);

        // Collision scanning: scan if cart has engine throttle, physical velocity, or displacement
        boolean isMoving = Math.abs(this.currentSpeed) > 0.01
                || this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-5
                || speedSq > 1.0E-5;
        if (isMoving) {
            double effectiveSpeed = Math.max(speedSq, Math.abs(this.currentSpeed));
            TerracartCollisionHandler.handleCollisions(this, effectiveSpeed);
            TerracartCollisionHandler.handleCartToCartCollisions(this, effectiveSpeed);
        }

        lastX = this.getX(); lastZ = this.getZ();

        super.tick();
    }

    // ================================================================================================================
    //    DELEGATED ACTIONS & COMPATIBILITY OVERRIDES
    // ================================================================================================================

    @Override
    protected void positionRider(@NonNull Entity passenger,  Entity.@NonNull MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);
        TerracartCompatibilityHandler.handlePositionRider(this, passenger);
    }

    @Override
    public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
        return EntityDimensions.scalable((float) TerracartCollisionHandler.CART_LENGTH, TerracartCollisionHandler.BOX_HEIGHT);
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        this.reconstructRectangularBox(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        this.reconstructRectangularBox(x, y, z);
    }

    @Override
    public void setYRot(float yaw) {
        if (TerracartCollisionHandler.canRotateTo(this, yaw)) {
            super.setYRot(yaw);
        }
    }

    private void reconstructRectangularBox(double x, double y, double z) {
        super.setBoundingBox(TerracartCollisionHandler.getCalculatedBoundingBox(x, y, z, this.getYRot()));
    }

    @Override
    public @NonNull InteractionResult interact(@NonNull Player player, @NonNull InteractionHand hand, @NonNull Vec3 pos) {
        return TerracartInteractionHandler.handleInteraction(this, player, hand);
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel sl, @NotNull DamageSource src, float amount) {
        if (this.isInvulnerableToBase(src)) {
            return false;
        }
        return TerracartInteractionHandler.handleHurtServer(this, sl, src, amount);
    }

    @Override
    public void push(@NonNull Entity entity) {
        TerracartCollisionHandler.performPush(this, entity);
    }

    @Override
    public void push(double x, double y, double z) {
        // Terracart is a heavy iron vehicle: damp raw external shoves (like vanilla player sprint hits)
        // and keep it firmly grounded (y = 0.0) so it never pops into the air or gets launched like a puck.
        double massFactor = 0.15; // 85% mass dampening against vanilla player/mob sprint pushes
        super.push(x * massFactor, 0.0, z * massFactor);
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
    public boolean displayFireAnimation() {
        return false;
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
