package com.ilyrac.terracart.item;

import com.ilyrac.terracart.component.ModDataComponents;
import com.ilyrac.terracart.component.TerracartCoreStats;
import com.ilyrac.terracart.entity.ModEntities;
import com.ilyrac.terracart.entity.TerracartEntity;
import com.ilyrac.terracart.tier.TerracartTier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Consumer;

public class TerracartItem extends Item {
    private final TerracartTier tier;

    public TerracartItem(TerracartTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public TerracartTier getTier() {
        return this.tier;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {
        TerracartCoreItem.appendStatsTooltip(this.tier, stack, tooltipAdder);
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        Vec3 clickPos = context.getClickLocation();
        ItemStack stack = context.getItemInHand();

        TerracartEntity entity = new TerracartEntity(ModEntities.getEntityTypeFor(this.tier), serverLevel);
        entity.setPos(clickPos.x, clickPos.y, clickPos.z);

        float yaw = 0.0F;
        if (context.getPlayer() != null) {
            yaw = Mth.wrapDegrees(context.getPlayer().getYRot());
        }

        entity.setYRot(yaw);
        entity.setXRot(0.0F);
        entity.yRotO = yaw;
        entity.xRotO = 0.0F;

        // Default neutral color (-1)
        entity.setCartColor(-1);

        // Core stats: inherit from calibrated item, or roll fresh random on first deployment
        TerracartCoreStats stats = stack.get(ModDataComponents.CORE_STATS);
        entity.setCoreStats(Objects.requireNonNullElseGet(stats, () -> TerracartCoreStats.forRandomTier(this.tier, serverLevel.getRandom())));
        entity.setHealth(entity.getMaxHealth());
        int startFuel = 5 + serverLevel.getRandom().nextInt(11); // 5 to 15 FU
        entity.setFuel(startFuel * TerracartEntity.TICKS_PER_FUEL_UNIT);

        serverLevel.addFreshEntity(entity);
        stack.shrink(1);

        return InteractionResult.SUCCESS;
    }
}