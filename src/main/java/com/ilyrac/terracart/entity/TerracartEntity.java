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
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.Block;
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

public class TerracartEntity extends VehicleEntity {

    /* -------------------- Construction -------------------- */

    public TerracartEntity(EntityType<? extends TerracartEntity> type, Level level) {
        super(type, level);

        // initialize last position trackers so first tick delta is sane
        this.lastX = this.getX();
        this.lastZ = this.getZ();
    }

    /* -------------------- Save / Load -------------------- */

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        output.putInt("CartColor", getCartColor());
        output.putInt("FuelTicks", fuelTicks);
        output.putFloat("Health", this.getHealth());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        int color = input.getIntOr("CartColor", -1);
        entityData.set(CART_COLOR, color);

        fuelTicks = input.getIntOr("FuelTicks", 0);
        entityData.set(FUEL_TICKS, fuelTicks);

        this.setHealth(input.getFloatOr("Health", MAX_HEALTH));
    }

    /* -------------------- Synced Data -------------------- */

    private static final EntityDataAccessor<Integer> CART_COLOR =
            SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> FUEL_TICKS =
            SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> WHEEL_ROTATION =
            SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Boolean> SOUND_ACTIVE =
            SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Float> SOUND_VOLUME =
            SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Float> SOUND_PITCH =
            SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> CURRENT_HEALTH =
            SynchedEntityData.defineId(TerracartEntity.class, EntityDataSerializers.FLOAT);

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

    /* -------------------- State -------------------- */

    private float driverForward = 0.0f;
    private float driverStrafe  = 0.0f;
    public void setDriverInput(float forward, float strafe) {
        this.driverForward = Mth.clamp(forward, -1.0f, 1.0f);
        this.driverStrafe  = Mth.clamp(strafe, -1.0f, 1.0f);
    }

    // Colored Carts
    public void setCartColor(int color) {
        if (color < 0) this.entityData.set(CART_COLOR, -1);
        else this.entityData.set(CART_COLOR, Math.min(15, color));
    }
    public int getCartColor() {
        return this.entityData.get(CART_COLOR);
    }

    // animation and speed
    private double currentSpeed = 0.0;
    private float prevWheelRotation = 0.0f;
    public float getWheelRotation() {
        return this.entityData.get(WHEEL_ROTATION);
    }
    public float getPrevWheelRotation() {
        return this.prevWheelRotation;
    }

    // speedometer
    private Vec3 lastPos = Vec3.ZERO;
    private float speedBps = 0.0F;
    public float getSpeedBps() { return speedBps; }

    // particles and sound
    private int engineSoundCooldown = 0;
    private double lastX;
    private double lastZ;

    // damage cooldown
    private int hitCooldown = 0;
    private int fireCooldown = 0;

    // Health
    private boolean wasAirborne = false;
    private double airborneStartY = 0.0;
    private static final float DAMAGE_REDUCTION = 1.2f;
    public static final float MAX_HEALTH = 100.0f;
    public float getHealth() {
        return this.entityData.get(CURRENT_HEALTH);
    }
    public void setHealth(float health) {
        this.entityData.set(CURRENT_HEALTH, Mth.clamp(health, 0.0f, MAX_HEALTH));
    }
    public float getHealthPercent() {
        return this.getHealth() / MAX_HEALTH;
    }

    /* -------------------- Fuel -------------------- */

    public static final int MAX_FUEL = 24000;
    private int fuelTicks = 1200;

    public void addFuel(int amount) {
        fuelTicks = Math.min(fuelTicks + amount, MAX_FUEL);
        entityData.set(FUEL_TICKS, fuelTicks);
    }

    public boolean hasFuel() { return fuelTicks > 0; }

    public float getFuelPercent() { return (float) fuelTicks / (float) MAX_FUEL; }

    /* -------------------- Item / Drops -------------------- */

    @Override
    protected @NonNull Item getDropItem() { return Items.AIR; }

    @Override
    protected void destroy(@NonNull ServerLevel level, @NonNull DamageSource source) {
        this.kill(level);

        if (!level.getGameRules().get(GameRules.ENTITY_DROPS)) return;

        int ironCount = 3 + this.random.nextInt(5);
        this.spawnAtLocation(level, new ItemStack(Items.IRON_INGOT, ironCount));
        this.spawnAtLocation(level, ModItems.TERRRACART_WHEEL);
        this.spawnAtLocation(level, Items.FURNACE);
    }

    @Override
    public boolean isPickable() { return true; }

    @Override
    public @NotNull ItemStack getPickResult() {
        int color = getCartColor();
        if (color >= 0 && color < ModItems.COLORED_TERRACARTS.length) {
            return new ItemStack(ModItems.COLORED_TERRACARTS[color]);
        }
        return new ItemStack(ModItems.TERRACART);
    }

    /* -------------------- Collision helpers -------------------- */

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) { return true; }

    @Override
    public boolean canCollideWith(@Nullable Entity entity) {
        return canVehicleCollide(this, entity);
    }

    public static boolean canVehicleCollide(Entity self, @Nullable Entity other) {
        if (other == null) return false;
        return (other.canBeCollidedWith(self) || other.isPushable())
                && !self.isPassengerOfSameVehicle(other);
    }

    @Override
    public boolean isPushable() { return true; }

    @Override
    public void push(@NonNull Entity entity) {
        if (!this.isPassengerOfSameVehicle(entity)) {
            Vec3 delta = entity.position().subtract(this.position());
            if (delta.lengthSqr() > 1.0E-4) {
                delta = delta.normalize().scale(0.002);
                this.setDeltaMovement(this.getDeltaMovement().subtract(delta));
            }
        }
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel serverLevel, @NotNull DamageSource source, float amount) {
        if (this.isRemoved() || this.isInvulnerableToBase(source)) return false;

        // Check if damage is coming from a creative player
        if (source.getEntity() instanceof Player player && player.isCreative()) {
            // destroy immediately, no drops
            serverLevel.playSound(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    ModSounds.TERRACART_HIT,
                    SoundSource.NEUTRAL,
                    0.5F,
                    1.0F + (this.random.nextFloat() - 0.5F) * 0.2F
            );
            serverLevel.sendParticles(
                    ParticleTypes.SQUID_INK,
                    this.getX(), this.getY() + 1, this.getZ(),
                    5, 0.1, 0.1, 0.1, 0.1
            );
            this.discard(); // removes entity
            return true;
        }

        float scaledAmount = amount * DAMAGE_REDUCTION;
        float newHealth = this.getHealth() - scaledAmount;
        this.setHealth(newHealth);

        // play crash sound + particles
        serverLevel.playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                ModSounds.TERRACART_HIT,
                SoundSource.NEUTRAL,
                0.5F,
                1.0F + (this.random.nextFloat() - 0.5F) * 0.2F
        );
        serverLevel.sendParticles(
                ParticleTypes.SQUID_INK,
                this.getX(), this.getY() + 1, this.getZ(),
                5, 0.1, 0.1, 0.1, 0.1
        );

        if (newHealth <= 0) {
            // regular destruction (drops)
            this.destroy(serverLevel, source);
        }

        return true;
    }

    /* -------------------- Riding -------------------- */

    @Override
    public @NonNull InteractionResult interact(@NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // -------- REPAIR WITH IRON BLOCK (full heal, only if below 50%) --------
        if (stack.is(Items.IRON_BLOCK)) {
            // refuse like fuel logic if health >= 50%
            if (this.getHealth() >= MAX_HEALTH * 0.5f) {
                player.displayClientMessage(
                        Component.literal("cannot repair, health is above 50%."),
                        true
                );
                return InteractionResult.SUCCESS;
            }

            // full repair
            this.setHealth(MAX_HEALTH);

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(
                        /* player */ null,
                        this.getX(), this.getY(), this.getZ(),
                        SoundEvents.IRON_GOLEM_REPAIR,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f + (this.random.nextFloat() - 0.5f) * 0.15f
                );

                serverLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        this.getX(),
                        this.getY() + 0.6,
                        this.getZ(),
                        12,   // count
                        0.25, 0.25, 0.25,
                        0.05
                );
            }

            if (!player.isCreative()) stack.shrink(1);

            player.displayClientMessage(
                    Component.literal("TerraCart fully repaired."),
                    true
            );

            return InteractionResult.SUCCESS;
        }


        if (stack.is(Items.COAL_BLOCK)) {
            if (getFuelPercent() >= 0.5f) {
                player.displayClientMessage(
                        Component.literal("cannot refuel, Fuel is above 50%."),
                        true
                );
                return InteractionResult.SUCCESS;
            }

            this.addFuel(MAX_FUEL);

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(
                        /* player */ null,
                        this.getX(), this.getY(), this.getZ(),
                        ModSounds.TERRACART_REFUEL,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );
                serverLevel.sendParticles(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        this.getX(),
                        this.getY() + 0.6,
                        this.getZ(),
                        6,        // count
                        0.25,      // spread X
                        0.15,      // spread Y
                        0.25,      // spread Z
                        0.1       // speed
                );
            }

            if (!player.isCreative()) stack.shrink(1);
            player.displayClientMessage(Component.literal("TerraCart refueled."), true);
            return InteractionResult.SUCCESS;
        }

        player.startRiding(this);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean canAddPassenger(@NonNull Entity passenger) {
        return passenger instanceof Player && this.getPassengers().isEmpty();
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        Entity e = this.getFirstPassenger();
        return e instanceof LivingEntity living ? living : null;
    }

    @Override
    public @NonNull Vec3 getPassengerRidingPosition(@NonNull Entity passenger) {
        return this.position().add(0.0, 0.45, 0.0);
    }

    @Override
    protected void removePassenger(@NonNull Entity passenger) {
        super.removePassenger(passenger);
    }

    @Override
    public boolean canRide(@NonNull Entity entity) {
        // TerraCart may NEVER ride anything
        return false;
    }

    @Override
    protected void playStepSound(@NonNull BlockPos pos, @NonNull BlockState state) {
        // suppress footstep sound + subtitle
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NonNull BlockState state, @NonNull BlockPos pos) {
        // avoid fall/landing sounds
    }


    /* -------------------- Dimensions -------------------- */

    @Override
    public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
        return EntityDimensions.fixed(2.0F, 1.0F);
    }

    /* -------------------- Physics helpers -------------------- */

    private Vec3 computeStepUp(Vec3 motion, boolean blockedByBlock) {
        if (!this.onGround()) return null;
        if (motion.horizontalDistanceSqr() < 1.0E-6) return null;

        AABB box = this.getBoundingBox();
        for (double y = 0.2; y <= 1.0; y += 0.2) {
            // move box up
            AABB upBox = box.move(0.0, y, 0.0);
            if (!this.level().noCollision(this, upBox)) continue;

            // move forward
            AABB forwardBox = upBox.move(motion.x, 0.0, motion.z);
            if (!this.level().noCollision(this, forwardBox)) continue;

            // If collision exists at current horizontal motion but step is possible → allow
            if (!blockedByBlock) continue;

            return new Vec3(motion.x, y, motion.z);
        }
        return null; // blocked, cannot step
    }

    /* -------------------- Particles -------------------- */

    private void spawnMovementParticles() {
        if (!this.onGround()) return;

        double dx = this.getX() - lastX;
        double dz = this.getZ() - lastZ;
        double speed = Math.sqrt(dx * dx + dz * dz);
        if (speed < 0.002) return;

        int count = Mth.clamp((int)(speed * 60 * 30), 4, 20);

        Vec3 motion = this.getDeltaMovement();
        double dirX = motion.x;
        double dirZ = motion.z;

        if (dirX * dirX + dirZ * dirZ < 1.0E-6 && speed > 0.0001) {
            dirX = dx / speed;
            dirZ = dz / speed;
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 1.2;
                double offsetZ = (this.random.nextDouble() - 0.5) * 1.2;
                double px = this.getX() + offsetX;
                double py = this.getY() + 0.05;
                double pz = this.getZ() + offsetZ;
                double vx = -dirX * 0.3 + (this.random.nextGaussian() * 0.01);
                double vy = 0.02 + this.random.nextDouble() * 0.02;
                double vz = -dirZ * 0.3 + (this.random.nextGaussian() * 0.01);

                serverLevel.sendParticles(
                        ParticleTypes.SMOKE,
                        px, py, pz,
                        1,
                        vx, vy, vz,
                        0.0
                );
            }
        }
    }

    private float getNewRotation(double dx, double dz) {
        float yawRad = (float) Math.toRadians(this.getYRot());
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);

        double signedDistance = dx * forwardX + dz * forwardZ;
        final float WHEEL_RADIUS = 0.35f;
        float deltaRotation = (float)(- signedDistance / WHEEL_RADIUS);

        return this.prevWheelRotation + deltaRotation;
    }

    /* -------------------- Main tick -------------------- */

    @Override
    public void tick() {
        super.tick();

        // previous wheel rotation for interpolation
        this.prevWheelRotation = this.getWheelRotation();

        // treat lava as a fluid too (so applyPlayerInputOrFriction will block throttle)
        boolean inFluid = this.isInWater() || this.isInLava();

        updateSpeedometer();
        syncAndBurnFuel();
        handleLavaCollision();
        handleFireDamage();

        LivingEntity controller = this.getControllingPassenger();

        // motion & movement
        Vec3 motion = this.getDeltaMovement();
        motion = applyGravityAndGround(motion);
        motion = applyPlayerInputOrFriction(controller, motion, inFluid);

        // apply motion and move
        this.setDeltaMovement(motion);
        this.move(MoverType.SELF, motion);

        handleStepUpIfNeeded(motion);
        handleFallDamageOnLand();

        // movement delta (used for particles/sound/wheel)
        double dx = this.getX() - lastX;
        double dz = this.getZ() - lastZ;
        double speed = Math.sqrt(dx * dx + dz * dz);

        // visuals & audio
        spawnMovementParticles();
        tickCooldowns();
        updateSoundState(speed);

        // server authoritative wheel rotation
        float newRotation = getNewRotation(dx, dz);
        this.entityData.set(WHEEL_ROTATION, newRotation);

        // save last position for next tick
        lastX = this.getX();
        lastZ = this.getZ();

        // collisions with entities (pushing / hurting)
        handleEntityCollisions(speed);
    }

    /* -------------------- Tick helpers -------------------- */

    private void handleBlockImpactBeforeMove(Vec3 motion) {
        // Only server-side and when not on cooldown
        if (this.level().isClientSide() || hitCooldown > 0) return;

        // horizontal motion only
        Vec3 horizontal = new Vec3(motion.x, 0.0, motion.z);
        double speed = horizontal.horizontalDistance();
        if (speed < 1.0E-6) return;

        // compute damage based on horizontal speed
        if (speed <= 0.35) {
            return;
        }

        // apply damage
        float damage = (float) Mth.clamp(speed * 10, 1.0f, 10.0f);
        this.setHealth(this.getHealth() - damage);

        // play crash sound + particles
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    ModSounds.TERRACART_CRASH,
                    SoundSource.NEUTRAL,
                    0.6F,
                    1.0F + (this.random.nextFloat() - 0.5F) * 0.2F
            );

            serverLevel.sendParticles(
                    ParticleTypes.SQUID_INK,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    6, 0.12, 0.12, 0.12, 0.08
            );
        }

        // destroy cart if health <= 0
        if (this.getHealth() <= 0.0f && this.level() instanceof ServerLevel serverLevel) {
            this.destroy(serverLevel, this.damageSources().generic());
        }

        hitCooldown = 30; // cooldown ~1 second
    }

    private boolean isInFireBlock() {
        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos);

        // Check if it's FIRE or CAMPFIRE (add more if needed)
        Block block = state.getBlock();
        return block == Blocks.FIRE
                || block == Blocks.CAMPFIRE
                || block == Blocks.SOUL_FIRE;
    }

    private void handleFireDamage() {
        if (this.level().isClientSide()) return; // server only

        if (fireCooldown > 0) {
            fireCooldown--;
            return;
        }

        if (isInFireBlock()) {
            float damage = 5.0f; // adjust damage per hit
            this.setHealth(this.getHealth() - damage);

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(
                        null,
                        this.getX(), this.getY(), this.getZ(),
                        ModSounds.TERRACART_HIT,
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F
                );
                serverLevel.sendParticles(
                        ParticleTypes.FLAME,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        6, 0.1, 0.1, 0.1, 0.05
                );
            }

            if (this.getHealth() <= 0.0f && this.level() instanceof ServerLevel serverLevel) {
                this.destroy(serverLevel, this.damageSources().inFire());
            }

            fireCooldown = 10;
        }
    }

    private void updateSoundState(double speed) {
        // whether the cart produce sound
        boolean active = speed > 0.01 && this.onGround() && hasFuel();

        // map speed -> target values (tweak constants to taste)
        float targetVolume = active ? Mth.clamp((float)(speed * 2.0), 0.0F, 1.0F) : 0.0F;
        float targetPitch  = active ? 1.0F + Mth.clamp((float)(speed * 0.6), 0.0F, 1.0F) : 1.0F;

        // smooth it
        float vol = this.entityData.get(SOUND_VOLUME);
        float pit = this.entityData.get(SOUND_PITCH);
        final float SERVER_LERP = 0.15f;

        vol += (targetVolume - vol) * SERVER_LERP;
        pit += (targetPitch - pit) * SERVER_LERP;

        if (Math.abs(vol) < 0.0005f) vol = 0.0f;

        this.entityData.set(SOUND_ACTIVE, active);
        this.entityData.set(SOUND_VOLUME, vol);
        this.entityData.set(SOUND_PITCH, pit);
    }

    private void updateSpeedometer() {
        Vec3 currentPos = this.position();
        if (lastPos != Vec3.ZERO) {
            double dx = currentPos.x - lastPos.x;
            double dz = currentPos.z - lastPos.z;
            double distance = Math.sqrt(dx * dx + dz * dz);
            speedBps = (float)(distance * 20.0);
        }
        lastPos = currentPos;
    }

    private void syncAndBurnFuel() {
        fuelTicks = entityData.get(FUEL_TICKS);
        if (fuelTicks > 0) fuelTicks--;
        entityData.set(FUEL_TICKS, fuelTicks);
    }

    private void handleLavaCollision() {
        if (this.level().isClientSide()) return;
        if (!this.isInLava()) return;

        // damage pacing (reuse fireCooldown)
        if (fireCooldown > 0) {
            fireCooldown--;
            return;
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            // route damage through your hurtServer so creative-destruction and drops remain consistent
            this.hurtServer(serverLevel, this.damageSources().lava(), 10.0f);

            serverLevel.playSound(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    ModSounds.TERRACART_HIT,
                    SoundSource.NEUTRAL,
                    0.8F,
                    0.9F + this.random.nextFloat() * 0.2F
            );

            serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    10, 0.25, 0.25, 0.25, 0.03
            );
        }

        fireCooldown = 10;
    }


    private Vec3 applyGravityAndGround(Vec3 motion) {
        final double GRAVITY = 1.0;
        final double TERMINAL_Y = -1.0;

        if (!this.onGround()) {
            motion = motion.add(0.0, -GRAVITY, 0.0);
            if (motion.y < TERMINAL_Y) motion = new Vec3(motion.x, TERMINAL_Y, motion.z);
        } else {
            if (motion.y < 0.0) motion = new Vec3(motion.x, 0.0, motion.z);
        }
        return motion;
    }

    private Vec3 applyPlayerInputOrFriction(LivingEntity controller, Vec3 motion, boolean inFluid) {
        final double MAX_SPEED = 1.0;
        final double MAX_BACKWARD_SPEED = 0.25;
        final double ACCELERATION = 0.005;
        final double FRICTION = 0.97;
        final double AIR_DRAG = 0.95;
        final float TURN_SPEED = 7.5F;

        boolean powered = hasFuel();

        // If not powered, slow normally
        if (!powered) this.currentSpeed *= FRICTION;

        if (inFluid) {
            // Glide factor: fraction of speed removed each tick.
            // Higher -> stops faster. Suggested: water=0.06, lava=0.12
            final double glideFactor = this.isInLava() ? 0.12 : 0.06;

            // Immediately start damping the engine speed so we don't re-add throttle
            this.currentSpeed = Mth.lerp(glideFactor, this.currentSpeed, 0.0);

            // Apply linear damping to the *total horizontal motion* (this damps pushes, previous velocity, etc.)
            Vec3 horizontal = new Vec3(motion.x, 0.0, motion.z);

            // Reduce horizontal velocity by (1 - glideFactor) each tick (linear glide)
            horizontal = horizontal.scale(Math.max(0.0, 1.0 - glideFactor));

            // small dead-zone: snap very small speeds to zero to avoid forever tiny drift
            if (horizontal.lengthSqr() < 1.0E-6) horizontal = Vec3.ZERO;

            // damp vertical less, keep some upward motion if present but cap falling speed a bit
            double newY = motion.y;
            // optional: cap downward velocity in fluid to avoid excessive fall-through
            if (newY < -0.6) newY = -0.6;

            // Compose new motion — NOTE: DO NOT add engine motion here (throttle is blocked in fluid)
            motion = new Vec3(horizontal.x, newY * 0.85, horizontal.z);

            return motion;
        }

        // Normal on-ground player control (server authoritative)
        if (controller instanceof Player player && powered) {
            float forward;
            float strafe;

            if (this.level().isClientSide()) {
                forward = player.zza;
                strafe  = -player.xxa;
            } else {
                forward = this.driverForward;
                strafe  = this.driverStrafe;
            }

            if (forward > 0) {
                if (currentSpeed < 0) currentSpeed += ACCELERATION * 3;
                else currentSpeed += ACCELERATION;
            } else if (forward < 0) {
                if (currentSpeed > 0) currentSpeed -= ACCELERATION * 6;
                else currentSpeed -= ACCELERATION;
                currentSpeed = Math.max(currentSpeed, -MAX_BACKWARD_SPEED);
            } else {
                currentSpeed *= FRICTION;
            }
            currentSpeed = Mth.clamp(currentSpeed, -MAX_SPEED, MAX_SPEED);

            if (Math.abs(currentSpeed) > 0.01 && strafe != 0) {
                float speedFactor = 1.0F - Math.min((float)(Math.abs(currentSpeed) / MAX_SPEED), 1.0F);
                float turn = TURN_SPEED * strafe * (0.3F + 0.7F * speedFactor);
                this.setYRot(this.getYRot() + turn);
            }

            if (controller instanceof Player) {
                float deltaYaw = Mth.wrapDegrees(this.getYRot() - this.yRotO);
                if (Math.abs(deltaYaw) > 0.0001F) {
                    player.setYRot(player.getYRot() + deltaYaw);
                    player.yRotO = player.getYRot();
                }
            }

            float yawRad = (float) Math.toRadians(this.getYRot());
            motion = new Vec3(
                    -Math.sin(yawRad) * currentSpeed,
                    motion.y,
                    Math.cos(yawRad) * currentSpeed
            );
        } else {
            // Default (air/friction)
            currentSpeed *= FRICTION;
            motion = new Vec3(motion.x * AIR_DRAG, motion.y, motion.z * AIR_DRAG);
        }

        return motion;
    }

    private void handleStepUpIfNeeded(Vec3 originalMotion) {
        // Detect whether a forward horizontal move is blocked by block collisions
        boolean blockedByBlock = this.horizontalCollision && this.level()
                .getBlockCollisions(this, this.getBoundingBox().move(originalMotion.x, 0.0, originalMotion.z))
                .iterator()
                .hasNext();

        if (this.horizontalCollision) {
            Vec3 stepMotion = computeStepUp(originalMotion, blockedByBlock);
            if (stepMotion != null) {
                // perform the small-step motion and clear any small vertical penetration
                this.setPos(this.xo, this.yo, this.zo);
                this.move(MoverType.SELF, stepMotion);
                if (this.onGround() && this.verticalCollision) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                }
            } else {
                // too tall or no valid step - apply slight slowdown (but damage was handled earlier)
                handleBlockImpactBeforeMove(originalMotion);
                currentSpeed *= 0.95;
            }
        }
    }

    private void tickCooldowns() {
        if (engineSoundCooldown > 0) engineSoundCooldown--;
        if (hitCooldown > 0) hitCooldown--;
    }

    private void handleEntityCollisions(double speed) {
        List<Entity> list = this.level().getEntities(
                this,
                this.getBoundingBox().inflate(0.2, -0.01, 0.2),
                EntitySelector.pushableBy(this)
        );

        for (Entity entity : list) {
            if (entity.hasPassenger(this)) continue;

            // push
            this.push(entity);

            // damage
            if (hitCooldown == 0 && speed > 0.1 &&
                entity instanceof LivingEntity living && living != this.getControllingPassenger()) {

                float damage = Mth.clamp((float)(speed * 6.0), 1.5F, 5.0F);

                //noinspection deprecation
                living.hurt(
                        this.damageSources().generic(),
                        damage
                );

                Vec3 knockback = entity.position().subtract(this.position()).normalize().scale(0.5 + speed);
                living.push(knockback.x, 0.15, knockback.z);

                hitCooldown = 20;
            }
        }
    }

    private void handleFallDamageOnLand() {
        // Run on both Client and Server
        if (this.wasAirborne && this.onGround()) {
            double fallDistance = this.airborneStartY - this.getY();

            if (fallDistance > 3.5) {
                // --- UPDATED LOGIC START ---
                // 15% speed loss per block past 3.5
                double impactSeverity = (fallDistance - 3.5) * 0.15;

                // Cap the loss at 90% (0.9).
                // This ensures speedRetention is at least 0.1 (10%)
                double cappedImpact = Math.min(impactSeverity, 0.9);
                double speedRetention = 1.0 - cappedImpact;

                this.currentSpeed *= speedRetention;
                // --- UPDATED LOGIC END ---

                // SERVER ONLY: Damage and effects
                if (!this.level().isClientSide()) {
                    float damage = (float)((fallDistance - 3.5) * 0.75);
                    damage = Mth.clamp(damage, 1.0f, 10.0f);

                    float newHealth = this.getHealth() - damage;
                    this.setHealth(newHealth);

                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(
                                null, this.getX(), this.getY(), this.getZ(),
                                ModSounds.TERRACART_CRASH,
                                SoundSource.NEUTRAL,
                                0.6F,
                                1.0F
                        );

                        serverLevel.sendParticles(
                                ParticleTypes.SQUID_INK,
                                this.getX(), this.getY() + 1.0, this.getZ(),
                                6, 0.12, 0.12, 0.12, 0.08);

                        if (newHealth <= 0.0f) {
                            this.destroy(serverLevel, this.damageSources().fall());
                        }
                    }
                    this.hitCooldown = Math.max(this.hitCooldown, 20);
                }
            }
            this.wasAirborne = false;
        }

        if (!this.onGround() && !this.wasAirborne) {
            this.wasAirborne = true;
            this.airborneStartY = this.getY();
        }
    }
}