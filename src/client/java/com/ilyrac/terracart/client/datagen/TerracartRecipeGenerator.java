package com.ilyrac.terracart.client.datagen;

import com.ilyrac.terracart.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class TerracartRecipeGenerator extends FabricRecipeProvider {

    public TerracartRecipeGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registries, @NonNull BootstrapContext<Recipe<?>> recipes, @NonNull BootstrapContext<Advancement> advancements) {
        return new RecipeProvider(recipes, advancements) {
            @Override
            public void buildRecipes() {
                // 1. Wheel recipe (unlocked strictly by Iron Ingot)
                shaped(RecipeCategory.MISC, ModItems.TERRRACART_WHEEL, 2)
                        .pattern(" B ")
                        .pattern("BIB")
                        .pattern(" B ")
                        .define('B', Items.WOOL.black())
                        .define('I', Items.IRON_INGOT)
                        .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                        .save(output);

                // 2. Tiered Carts (using respective Tier Core)
                // Minecart unlocks all cart recipes; each core unlocks its own specific recipe
                buildCartRecipe(ModItems.STANDARD_TERRACART, ModItems.STANDARD_TERRACART_CORE);
                buildCartRecipe(ModItems.SWIFT_TERRACART, ModItems.SWIFT_TERRACART_CORE);
                buildCartRecipe(ModItems.STURDY_TERRACART, ModItems.STURDY_TERRACART_CORE);
                buildCartRecipe(ModItems.PRIME_TERRACART, ModItems.PRIME_TERRACART_CORE);
            }

            private void buildCartRecipe(Item cart, Item core) {
                shaped(RecipeCategory.TRANSPORTATION, cart)
                        .pattern("WSW")
                        .pattern("CMG")
                        .pattern("WIW")
                        .define('W', ModItems.TERRRACART_WHEEL)
                        .define('S', Items.SADDLE)
                        .define('C', core)
                        .define('M', Items.MINECART)
                        .define('G', Items.GLASS_PANE)
                        .define('I', Items.IRON_INGOT)
                        .unlockedBy("has_minecart", has(Items.MINECART))
                        .unlockedBy("has_core", has(core))
                        .save(output);
            }
        };
    }
}
