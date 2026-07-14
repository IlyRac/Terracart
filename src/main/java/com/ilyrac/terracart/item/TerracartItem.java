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
    private final int colorId;

    // Standard uncolored constructor
    public TerracartItem(Properties properties) {
        this(-1, properties);
    }

    // Colored constructor
    public TerracartItem(int colorId, Properties properties) {
        super(properties);
        this.colorId = colorId;
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        Vec3 clickPos = context.getClickLocation();

        TerracartEntity entity = new TerracartEntity(ModEntities.TERRACART, serverLevel);
        entity.setPos(clickPos.x, clickPos.y, clickPos.z);

        float yaw = 0.0F;
        if (context.getPlayer() != null) {
            yaw = Mth.wrapDegrees(context.getPlayer().getYRot());
        }

        entity.setYRot(yaw);
        entity.setXRot(0.0F);
        entity.yRotO = yaw;
        entity.xRotO = 0.0F;

        // Apply the color ID (-1 defaults to default styling)
        entity.setCartColor(this.colorId);

        serverLevel.addFreshEntity(entity);
        context.getItemInHand().shrink(1);

        return InteractionResult.SUCCESS;
    }
}