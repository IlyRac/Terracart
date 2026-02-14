package com.ilyrac.terracart.entity;

import com.ilyrac.terracart.ModSounds;
import com.ilyrac.terracart.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.entity.vehicle.boat.AbstractBoat.canVehicleCollide;

public class TerracartEntity extends VehicleEntity {

    // ================================================================================================================
    //    1. CONSTANTS & SYNCED DATA
    // ================================================================================================================

    public static final float MAX_HEALTH = 100.0f;
    public static final int MAX_FUEL = 24000;

    private static final EntityDataAccessor<Integer> CART_COLOR = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FUEL_TICKS = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> WHEEL_ROTATION = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> SOUND_ACTIVE = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> SOUND_VOLUME = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SOUND_PITCH = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> CURRENT_HEALTH = SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);

    // ================================================================================================================
    //    2. FIELDS (STATE)
    // ================================================================================================================

    // Movement State
    private float driverForward = 0.0f;
    private float driverStrafe = 0.0f;
    private double currentSpeed = 0.0;
    private double lastX, lastZ;

    // Visuals & Audio
    private float prevWheelRotation = 0.0f;
    private int MovingSoundCooldown = 0;
    private Vec3 lastPos = Vec3.ZERO;
    private float speedBps = 0.0F;

    // Cooldowns
    private int hitCooldown = 0;
    private int fireCooldown = 0;

    // Falling Logic
    private boolean wasAirborne = false;
    private double airborneStartY = 0.0;

    // ================================================================================================================
    //    3. CONSTRUCTION & DATA SYNC
    // ================================================================================================================

    public TerracartEntity(EntityType<? extends TerracartEntity> type, Level level) {
        super(type, level);
        this.lastX = this.getX();
        this.lastZ = this.getZ();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CART_COLOR, -1);
        builder.define(FUEL_TICKS, 1200);
        builder.define(WHEEL_ROTATION, 0.0f);
        builder.define(SOUND_ACTIVE, false);
        builder.define(SOUND_VOLUME, 1.0f);
        builder.define(SOUND_PITCH, 1.0f);
        builder.define(CURRENT_HEALTH, MAX_HEALTH);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        output.putInt("CartColor", getCartColor());
        output.putInt("FuelTicks", this.getFuel());
        output.putFloat("Health", this.getHealth());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        this.setCartColor(input.getIntOr("CartColor", -1));
        this.setFuel(input.getIntOr("FuelTicks", 0));
        this.setHealth(input.getFloatOr("Health", MAX_HEALTH));
    }

    // ================================================================================================================
    //    4. MAIN TICK LOOP
    // ================================================================================================================

    @Override
    public void tick() {
        super.tick();

        this.prevWheelRotation = this.getWheelRotation();
        boolean inFluid = this.isInWater() || this.isInLava();

        // 1. Logic Updates
        updateSpeedometer();
        syncAndBurnFuel(); // Fuel only burns if moving
        tickCooldowns();

        // 2. Environmental Damage
        handleLavaCollision();
        handleFireDamage();

        // 3. Movement Physics
        LivingEntity controller = this.getControllingPassenger();
        Vec3 motion = this.getDeltaMovement();
        motion = applyGravityAndGround(motion);
        motion = applyPlayerInputOrFriction(controller, motion, inFluid);

        this.setDeltaMovement(motion);
        this.move(MoverType.SELF, motion);

        // 4. Post-Movement Checks
        handleStepUpIfNeeded(motion);
        handleFallDamageOnLand();

        // 5. Visuals & Audio
        double dx = this.getX() - lastX;
        double dz = this.getZ() - lastZ;
        double speed = Math.sqrt(dx * dx + dz * dz);

        spawnMovementParticles();
        updateSoundState(speed);

        // 6. Wheel Rotation (Server Authoritative)
        float newRotation = getNewRotation(dx, dz);
        this.entityData.set(WHEEL_ROTATION, newRotation);

        // 7. Entity Collisions
        handleEntityCollisions(speed);

        // 8. Update trackers
        lastX = this.getX();
        lastZ = this.getZ();
    }

    // ================================================================================================================
    //    5. INTERACTION (Fuel & Repair)
    // ================================================================================================================

    @Override
    public @NonNull InteractionResult interact(@NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // --- REPAIR (Iron Ingot) ---
        if (stack.is(Items.IRON_INGOT)) {
            if (this.getHealth() >= MAX_HEALTH) {
                player.displayClientMessage(Component.literal("Terracart is already fully repaired."), true);
                return InteractionResult.SUCCESS;
            }

            float healAmount = MAX_HEALTH * 0.25f;
            this.setHealth(this.getHealth() + healAmount);
            this.playRepairEffects();

            if (!player.isCreative()) stack.shrink(1);
            player.displayClientMessage(Component.literal("Terracart repaired (+25%)"), true);
            return InteractionResult.SUCCESS;
        }

        // --- REFUEL (Coal) ---
        if (stack.is(Items.COAL)) {
            int currentFuel = this.getFuel();

            // 90% Buffer Check (Prevents waste)
            if (currentFuel > (MAX_FUEL * 0.90)) {
                player.displayClientMessage(Component.literal("Fuel tank is nearly full!"), true);
                return InteractionResult.SUCCESS;
            }

            this.addFuel((int) (MAX_FUEL * 0.25));
            this.playRefuelEffects();

            if (!player.isCreative()) stack.shrink(1);
            player.displayClientMessage(Component.literal("TerraCart refueled (+25%)"), true);
            return InteractionResult.SUCCESS;
        }

        player.startRiding(this);
        return InteractionResult.SUCCESS;
    }

    private void playRepairEffects() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.TERRACART_REPAIR, SoundSource.BLOCKS, 1.0f, 1.0f + (this.random.nextFloat() - 0.5f) * 0.15f);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    this.getX(), this.getY() + 0.6, this.getZ(), 12, 0.25, 0.25, 0.25, 0.05);
        }
    }

    private void playRefuelEffects() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.TERRACART_REFUEL, SoundSource.BLOCKS, 1.0f, 1.0f);
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.getX(), this.getY() + 0.6, this.getZ(), 6, 0.25, 0.15, 0.25, 0.1);
        }
    }

    // ================================================================================================================
    //    6. FUEL LOGIC
    // ================================================================================================================

    private void syncAndBurnFuel() {
        int fuel = this.getFuel();
        // Only burn if moving significantly (horizontal distance)
        boolean isMoving = this.getDeltaMovement().horizontalDistanceSqr() > 0.0001;

        if (fuel > 0 && isMoving) {
            fuel--;
        }
        this.entityData.set(FUEL_TICKS, fuel);
    }

    public void addFuel(int amount) {
        this.entityData.set(FUEL_TICKS, Math.min(this.getFuel() + amount, MAX_FUEL));
    }

    public boolean hasFuel() { return this.getFuel() > 0; }
    public int getFuel() { return this.entityData.get(FUEL_TICKS); }
    public void setFuel(int ticks) { this.entityData.set(FUEL_TICKS, ticks); }
    public float getFuelPercent() { return (float) this.getFuel() / (float) MAX_FUEL; }

    // ================================================================================================================
    //    7. HEALTH & DAMAGE LOGIC
    // ================================================================================================================

    @Override
    public boolean hurtServer(@NotNull ServerLevel serverLevel, @NotNull DamageSource source, float amount) {
        if (this.isRemoved() || this.isInvulnerableToBase(source)) return false;

        this.setHealth(this.getHealth() - (amount * 0.7f));
        this.playHitEffects(serverLevel);

        if (this.getHealth() <= 0) {
            this.destroy(serverLevel, source);
        }
        return true;
    }

    private void handleBlockImpactBeforeMove(Vec3 motion) {
        if (this.level().isClientSide() || hitCooldown > 0) return;

        double speed = new Vec3(motion.x, 0.0, motion.z).horizontalDistance();
        if (speed <= 0.3) return;

        float damage = (float) Mth.clamp(speed * 6, 1.0f, 6.0f);
        this.setHealth(this.getHealth() - damage);

        if (this.level() instanceof ServerLevel sl) {
            sl.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.TERRACART_CRASH, SoundSource.NEUTRAL, 0.6F, 1.0F);
            sl.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY() + 1.0, this.getZ(), 6, 0.12, 0.12, 0.12, 0.08);
            if (this.getHealth() <= 0.0f) this.destroy(sl, this.damageSources().generic());
        }
        hitCooldown = 30;
    }

    private void handleFireDamage() {
        if (this.level().isClientSide() || fireCooldown > 0) return;

        if (isInFireBlock()) {
            this.setHealth(this.getHealth() - 5.0f);
            if (this.level() instanceof ServerLevel sl) {
                this.playHitEffects(sl);
                if (this.getHealth() <= 0) this.destroy(sl, this.damageSources().inFire());
            }
            fireCooldown = 15;
        }
    }

    private void handleLavaCollision() {
        if (this.level().isClientSide() || !this.isInLava() || fireCooldown > 0) return;

        this.setHealth(this.getHealth() - 10.0f);
        if (this.level() instanceof ServerLevel sl) {
            this.playHitEffects(sl);
            if (this.getHealth() <= 0) this.destroy(sl, this.damageSources().inFire());
        }
        fireCooldown = 20;
    }

    private boolean isInFireBlock() {
        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos);
        return state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE) || state.is(Blocks.CAMPFIRE);
    }

    private void playHitEffects(ServerLevel level) {
        level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.TERRACART_HIT, SoundSource.NEUTRAL, 1.0F, 1.0F + (this.random.nextFloat() - 0.5F) * 0.2F);
        level.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY() + 1, this.getZ(), 5, 0.1, 0.1, 0.1, 0.1);
    }

    // ================================================================================================================
    //    8. PHYSICS & MOVEMENT
    // ================================================================================================================

    private Vec3 applyPlayerInputOrFriction(LivingEntity controller, Vec3 motion, boolean inFluid) {
        final double MAX_SPEED = 1.0;
        final double ACCELERATION = 0.005;
        final double FRICTION = 0.97;

        if (inFluid) return handleFluidPhysics(motion);

        boolean powered = hasFuel();
        if (!powered) this.currentSpeed *= FRICTION;

        if (controller instanceof Player player && powered) {
            float forward = this.level().isClientSide() ? player.zza : this.driverForward;
            float strafe = this.level().isClientSide() ? -player.xxa : this.driverStrafe;

            // Throttle Logic
            if (forward > 0) currentSpeed += (currentSpeed < 0 ? ACCELERATION * 3 : ACCELERATION);
            else if (forward < 0) currentSpeed -= (currentSpeed > 0 ? ACCELERATION * 6 : ACCELERATION);
            else currentSpeed *= FRICTION;

            currentSpeed = Mth.clamp(currentSpeed, -MAX_SPEED, MAX_SPEED);

            // Turning Logic
            if (Math.abs(currentSpeed) > 0.01 && strafe != 0) {
                float turnSpeed = 7.5F * strafe * (0.3F + 0.7F * (1.0F - Math.min((float)(Math.abs(currentSpeed) / MAX_SPEED), 1.0F)));
                this.setYRot(this.getYRot() + turnSpeed);
            }

            // Sync player rotation slightly
            if (Math.abs(Mth.wrapDegrees(this.getYRot() - this.yRotO)) > 0.0001F) {
                player.setYRot(player.getYRot() + Mth.wrapDegrees(this.getYRot() - this.yRotO));
                player.yRotO = player.getYRot();
            }

            // Convert Speed/Yaw to Motion Vector
            float yawRad = (float) Math.toRadians(this.getYRot());
            return new Vec3(-Math.sin(yawRad) * currentSpeed, motion.y, Math.cos(yawRad) * currentSpeed);
        } else {
            // No Driver or No Fuel
            currentSpeed *= FRICTION;
            return new Vec3(motion.x * 0.95, motion.y, motion.z * 0.95);
        }
    }

    private Vec3 handleFluidPhysics(Vec3 motion) {
        double glideFactor = this.isInLava() ? 0.12 : 0.06;
        this.currentSpeed = Mth.lerp(glideFactor, this.currentSpeed, 0.0);
        Vec3 horizontal = new Vec3(motion.x, 0.0, motion.z).scale(Math.max(0.0, 1.0 - glideFactor));
        if (horizontal.lengthSqr() < 1.0E-6) horizontal = Vec3.ZERO;
        return new Vec3(horizontal.x, Math.max(motion.y, -0.6) * 0.85, horizontal.z);
    }

    private Vec3 applyGravityAndGround(Vec3 motion) {
        if (!this.onGround()) {
            motion = motion.add(0.0, -1.0, 0.0); // Gravity
            if (motion.y < -1.0) motion = new Vec3(motion.x, -1.0, motion.z); // Terminal Velocity
        } else if (motion.y < 0.0) {
            motion = new Vec3(motion.x, 0.0, motion.z);
        }
        return motion;
    }

    private void handleStepUpIfNeeded(Vec3 originalMotion) {
        if (!this.horizontalCollision) return;

        boolean blockedByBlock = this.level().getBlockCollisions(this, this.getBoundingBox().move(originalMotion.x, 0.0, originalMotion.z)).iterator().hasNext();
        Vec3 stepMotion = computeStepUp(originalMotion, blockedByBlock);

        if (stepMotion != null) {
            this.setPos(this.xo, this.yo, this.zo);
            this.move(MoverType.SELF, stepMotion);
            if (this.onGround()) this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
        } else {
            if (blockedByBlock) {
                handleBlockImpactBeforeMove(originalMotion);
                currentSpeed *= 0.95;
            } else {
                currentSpeed *= 0.5; // Entity collision slowdown
            }
        }
    }

    private Vec3 computeStepUp(Vec3 motion, boolean blockedByBlock) {
        if (!this.onGround() || motion.horizontalDistanceSqr() < 1.0E-6) return null;
        AABB box = this.getBoundingBox();
        for (double y = 0.2; y <= 1.0; y += 0.2) {
            if (!this.level().noCollision(this, box.move(0, y, 0))) continue;
            if (!this.level().noCollision(this, box.move(motion.x, y, motion.z))) continue;
            if (!blockedByBlock) continue;
            return new Vec3(motion.x, y, motion.z);
        }
        return null;
    }

    private void handleFallDamageOnLand() {
        if (this.wasAirborne && this.onGround()) {
            double fallDistance = this.airborneStartY - this.getY();
            if (fallDistance > 3.5) {
                // Speed Loss
                double impactSeverity = (fallDistance - 3.5) * 0.15;
                this.currentSpeed *= (1.0 - Math.min(impactSeverity, 0.9));

                // Damage (Server Only)
                if (!this.level().isClientSide()) {
                    float damage = Mth.clamp((float)((fallDistance - 3.5) * 0.5), 1.0f, 6.0f);
                    this.setHealth(this.getHealth() - damage);
                    if (this.level() instanceof ServerLevel sl) {
                        sl.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.TERRACART_CRASH, SoundSource.NEUTRAL, 0.6F, 1.0F);
                        sl.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY() + 1.0, this.getZ(), 6, 0.12, 0.12, 0.12, 0.08);
                        if (this.getHealth() <= 0) this.destroy(sl, this.damageSources().fall());
                    }
                    this.hitCooldown = Math.max(this.hitCooldown, 20);
                }
            }
            this.wasAirborne = false;
        } else if (!this.onGround() && !this.wasAirborne) {
            this.wasAirborne = true;
            this.airborneStartY = this.getY();
        }
    }

    // ================================================================================================================
    //    9. COLLISIONS & DROPS
    // ================================================================================================================

    private void handleEntityCollisions(double speed) {
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0.2, -0.01, 0.2), EntitySelector.pushableBy(this));
        for (Entity entity : list) {
            if (entity.hasPassenger(this)) continue;
            this.push(entity);
            if (hitCooldown == 0 && speed > 0.1 && entity instanceof LivingEntity living && living != this.getControllingPassenger()) {
                if (entity.position().subtract(this.position()).lengthSqr() <= 1.0E-6) continue;

                if (!this.level().isClientSide()) {
                    float damage = Mth.clamp((float)(speed * 6.0), 1.5F, 5.0F);
                    //noinspection deprecation
                    living.hurt(this.damageSources().generic(), damage);
                    Vec3 kb = entity.position().subtract(this.position()).normalize();
                    living.push(kb.x * (0.5 + Math.min(speed, 2.0)), 0.15, kb.z * (0.5 + Math.min(speed, 2.0)));
                }
                hitCooldown = 20;
            }
        }
    }

    @Override
    public void push(@NonNull Entity entity) {
        if (!this.level().isClientSide() && !this.isRemoved()) {
            double dx = entity.getX() - this.getX();
            double dz = entity.getZ() - this.getZ();
            double distance = Mth.absMax(dx, dz);

            if (distance >= 0.01) {
                distance = Math.sqrt(distance);
                dx /= distance;
                dz /= distance;
                double inverseDist = 1.0 / distance;
                if (inverseDist > 1.0) inverseDist = 1.0;

                dx *= inverseDist;
                dz *= inverseDist;

                dx *= 0.001;
                dz *= 0.001;

                // Apply reduced force to the cart
                this.push(-dx, 0.0, -dz);

                // Apply force to the thing hitting the cart (makes it feel solid)
                entity.push(dx, 0.0, dz);
            }
        }
    }

    @Override
    protected void destroy(@NonNull ServerLevel level, @NonNull DamageSource source) {
        this.kill(level);
        if (!level.getGameRules().get(GameRules.ENTITY_DROPS)) return;
        this.spawnAtLocation(level, new ItemStack(Items.IRON_INGOT, 3 + this.random.nextInt(5)));
        this.spawnAtLocation(level, ModItems.TERRRACART_WHEEL);
        this.spawnAtLocation(level, Items.FURNACE);
    }

    @Override public @NotNull ItemStack getPickResult() {
        int color = getCartColor();
        return (color >= 0 && color < ModItems.COLORED_TERRACARTS.length) ? new ItemStack(ModItems.COLORED_TERRACARTS[color]) : new ItemStack(ModItems.TERRACART);
    }

    @Override protected @NonNull Item getDropItem() { return Items.AIR; }
    @Override public boolean isPickable() { return true; }
    @Override public boolean isPushable() { return true; }
    @Override public boolean canBeCollidedWith(@Nullable Entity entity) { return true; }
    @Override public boolean canCollideWith(@Nullable Entity entity) {
        assert entity != null;
        return canVehicleCollide(this, entity); }

    // ================================================================================================================
    //    10. GETTERS / SETTERS & HELPERS
    // ================================================================================================================

    public void setDriverInput(float forward, float strafe) {
        this.driverForward = Mth.clamp(forward, -1.0f, 1.0f);
        this.driverStrafe = Mth.clamp(strafe, -1.0f, 1.0f);
    }

    public void setCartColor(int color) { this.entityData.set(CART_COLOR, (color < 0) ? -1 : Math.min(15, color)); }
    public int getCartColor() { return this.entityData.get(CART_COLOR); }

    public float getHealth() { return this.entityData.get(CURRENT_HEALTH); }
    public void setHealth(float health) { this.entityData.set(CURRENT_HEALTH, Mth.clamp(health, 0.0f, MAX_HEALTH)); }
    public float getHealthPercent() { return this.getHealth() / MAX_HEALTH; }

    public float getWheelRotation() { return this.entityData.get(WHEEL_ROTATION); }
    public float getPrevWheelRotation() { return this.prevWheelRotation; }
    public float getSpeedBps() { return speedBps; }

    // RIDING
    @Override protected boolean canAddPassenger(@NonNull Entity passenger) { return passenger instanceof Player && this.getPassengers().isEmpty(); }
    @Override public @Nullable LivingEntity getControllingPassenger() { return this.getFirstPassenger() instanceof LivingEntity living ? living : null; }
    @Override public @NonNull Vec3 getPassengerRidingPosition(@NonNull Entity passenger) { return this.position().add(0.0, 0.45, 0.0); }
    @Override public boolean canRide(@NonNull Entity entity) { return false; }

    // VISUALS
    private void updateSoundState(double speed) {
        boolean active = speed > 0.01 && this.onGround() && hasFuel();
        float targetVolume = active ? Mth.clamp((float)(speed * 2.0), 0.0F, 1.0F) : 0.0F;
        float targetPitch = active ? 1.0F + Mth.clamp((float)(speed * 0.6), 0.0F, 1.0F) : 1.0F;

        float vol = this.entityData.get(SOUND_VOLUME);
        float pit = this.entityData.get(SOUND_PITCH);
        vol += (targetVolume - vol) * 0.15f;
        pit += (targetPitch - pit) * 0.15f;
        if (Math.abs(vol) < 0.0005f) vol = 0.0f;

        this.entityData.set(SOUND_ACTIVE, active);
        this.entityData.set(SOUND_VOLUME, vol);
        this.entityData.set(SOUND_PITCH, pit);
    }

    private void updateSpeedometer() {
        if (lastPos != Vec3.ZERO) {
            double d = this.position().distanceTo(lastPos);
            speedBps = (float)(d * 20.0);
        }
        lastPos = this.position();
    }

    private void tickCooldowns() {
        if (MovingSoundCooldown > 0) MovingSoundCooldown--;
        if (hitCooldown > 0) hitCooldown--;
        if (fireCooldown > 0) fireCooldown--;
    }

    private void spawnMovementParticles() {
        if (!this.onGround() || !(this.level() instanceof ServerLevel sl)) return;
        double dx = this.getX() - lastX;
        double dz = this.getZ() - lastZ;
        double speed = Math.sqrt(dx * dx + dz * dz);
        if (speed < 0.002) return;

        int count = Mth.clamp((int)(speed * 60 * 30), 4, 20);
        double dirX = (speed > 0.0001) ? dx / speed : this.getDeltaMovement().x;
        double dirZ = (speed > 0.0001) ? dz / speed : this.getDeltaMovement().z;

        for (int i = 0; i < count; i++) {
            sl.sendParticles(ParticleTypes.SMOKE,
                    this.getX() + (this.random.nextDouble() - 0.5) * 1.2,
                    this.getY() + 0.05,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 1.2,
                    1, -dirX * 0.3, 0.03, -dirZ * 0.3, 0.0);
        }
    }

    private float getNewRotation(double dx, double dz) {
        float yawRad = (float) Math.toRadians(this.getYRot());
        double signedDistance = dx * (-Math.sin(yawRad)) + dz * Math.cos(yawRad);
        return this.prevWheelRotation + (float)(-signedDistance / 0.35f);
    }

    @Override public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) { return EntityDimensions.fixed(2.0F, 1.0F); }
    @Override protected void playStepSound(@NonNull BlockPos pos, @NonNull BlockState state) { /* suppressed */ }
    @Override protected void checkFallDamage(double y, boolean onGround, @NonNull BlockState state, @NonNull BlockPos pos) { /* suppressed */ }
}