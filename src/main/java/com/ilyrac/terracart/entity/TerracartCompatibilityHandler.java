package com.ilyrac.terracart.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import static net.minecraft.world.entity.vehicle.boat.AbstractBoat.canVehicleCollide;

public class TerracartCompatibilityHandler {

    public static boolean canAddPassenger(TerracartEntity cart, Entity passenger) {
        return passenger instanceof Player && cart.getPassengers().isEmpty();
    }

    public static @Nullable LivingEntity getControllingPassenger(TerracartEntity cart) {
        return cart.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    public static Vec3 getPassengerRidingPosition(TerracartEntity cart, Entity passenger) {
        return cart.position().add(0.0, 0.6, 0.0);
    }

    public static boolean canRide(TerracartEntity cart, Entity entity) {
        return false;
    }

    public static Item getDropItem() {
        return Items.AIR;
    }

    public static boolean isPickable(TerracartEntity cart) {
        return true;
    }

    public static boolean isPushable(TerracartEntity cart) {
        return true;
    }

    public static boolean canBeCollidedWith(TerracartEntity cart, @Nullable Entity entity) {
        return true;
    }

    public static boolean canCollideWith(TerracartEntity cart, @Nullable Entity entity) {
        return entity != null && canVehicleCollide(cart, entity);
    }

    public static EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(2.0F, 1.0F);
    }
}