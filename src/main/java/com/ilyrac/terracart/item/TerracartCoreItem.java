package com.ilyrac.terracart.item;

import com.ilyrac.terracart.component.ModDataComponents;
import com.ilyrac.terracart.component.TerracartCoreStats;
import com.ilyrac.terracart.tier.TerracartTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class TerracartCoreItem extends Item {
    private final TerracartTier tier;

    public TerracartCoreItem(TerracartTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public TerracartTier getTier() {
        return tier;
    }

    public static void appendStatsTooltip(TerracartTier tier, ItemStack stack, Consumer<Component> tooltipAdder) {
        TerracartCoreStats stats = stack.get(ModDataComponents.CORE_STATS);
        if (stats != null) {
            tooltipAdder.accept(Component.literal("Speed: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f bps", stats.maxSpeed())).withStyle(ChatFormatting.AQUA)));
            tooltipAdder.accept(Component.literal("Fuel: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(stats.maxFuel() + "%").withStyle(ChatFormatting.GOLD)));
            tooltipAdder.accept(Component.literal("Condition: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(stats.maxCondition() + " HP").withStyle(ChatFormatting.GREEN)));
        } else {
            tooltipAdder.accept(Component.literal("Speed: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f - %.1f bps", (float) tier.getMinSpeed(), (float) tier.getMaxSpeed())).withStyle(ChatFormatting.AQUA)));
            tooltipAdder.accept(Component.literal("Fuel: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(tier.getMinFuel() + " - " + tier.getMaxFuel() + "%").withStyle(ChatFormatting.GOLD)));
            tooltipAdder.accept(Component.literal("Condition: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(tier.getMinCondition() + " - " + tier.getMaxCondition() + " HP").withStyle(ChatFormatting.GREEN)));
        }
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {
        appendStatsTooltip(this.tier, stack, tooltipAdder);
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
    }
}
