package com.ilyrac.terracart.item;

import com.ilyrac.terracart.entity.ModEntities;
import com.ilyrac.terracart.entity.TerracartEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class TerracartItem extends Item {

    public TerracartItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        // 🚫 client never spawns entities
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        TerracartEntity entity = getTerraCartEntity(context, serverLevel);

        serverLevel.addFreshEntity(entity);

        context.getItemInHand().shrink(1);

        return InteractionResult.SUCCESS;
    }

    private static @NonNull TerracartEntity getTerraCartEntity(UseOnContext context, ServerLevel serverLevel) {
        Vec3 clickPos = context.getClickLocation();

        // CONSTRUCTION
        TerracartEntity entity = new TerracartEntity(
                ModEntities.TERRACART,
                serverLevel
        );

        // Positioning
        entity.setPos(
                clickPos.x,
                clickPos.y,
                clickPos.z
        );

        // rotation
        float yaw = 0.0F;

        if (context.getPlayer() != null) {
            yaw = Mth.wrapDegrees(context.getPlayer().getYRot());
        }

        entity.setYRot(yaw);
        entity.setXRot(0.0F);

        // sync previous rotation
        entity.yRotO = yaw;
        entity.xRotO = 0.0F;

        return entity;
    }
}
