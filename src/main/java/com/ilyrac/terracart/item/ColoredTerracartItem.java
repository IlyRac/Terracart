package com.ilyrac.terracart.item;

import com.ilyrac.terracart.entity.ModEntities;
import com.ilyrac.terracart.entity.TerraCartEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class ColoredTerracartItem extends Item {
    private final int colorId;

    public ColoredTerracartItem(int colorId, Properties props) {
        super(props);
        this.colorId = colorId;
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        // server spawns only
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ServerLevel serverLevel = (ServerLevel) level;
        Vec3 clickPos = context.getClickLocation();

        // Create entity
        TerraCartEntity entity = new TerraCartEntity(ModEntities.TERRACART, serverLevel);
        entity.setPos(clickPos.x, clickPos.y, clickPos.z);

        float yaw = 0.0F;
        if (context.getPlayer() != null) {
            yaw = Mth.wrapDegrees(context.getPlayer().getYRot());
        }

        entity.setYRot(yaw);
        entity.setXRot(0.0F);
        entity.yRotO = yaw;
        entity.xRotO = 0.0F;

        // Set the cart color id (server authoritative)
        entity.setCartColor(this.colorId);

        // Spawn and consume
        serverLevel.addFreshEntity(entity);
        context.getItemInHand().shrink(1);

        return InteractionResult.SUCCESS;
    }
}
