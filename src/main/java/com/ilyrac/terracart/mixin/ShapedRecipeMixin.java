package com.ilyrac.terracart.mixin;

import com.ilyrac.terracart.component.ModDataComponents;
import com.ilyrac.terracart.component.TerracartCoreStats;
import com.ilyrac.terracart.item.TerracartCoreItem;
import com.ilyrac.terracart.item.TerracartItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin {

    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void terracart$copyCoreStats(CraftingInput input, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result != null && result.getItem() instanceof TerracartItem) {
            for (ItemStack ingredient : input.items()) {
                if (ingredient.getItem() instanceof TerracartCoreItem) {
                    TerracartCoreStats stats = ingredient.get(ModDataComponents.CORE_STATS);
                    if (stats != null) {
                        result.set(ModDataComponents.CORE_STATS, stats);
                    }
                    break;
                }
            }
        }
    }
}
