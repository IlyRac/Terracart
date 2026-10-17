package com.ilyrac.terracart.client.datagen;

import com.ilyrac.terracart.Terracart;
import com.ilyrac.terracart.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class TerracartModelGenerator extends FabricModelProvider {

    public TerracartModelGenerator(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators blockModelGenerators) {
        // No custom blocks in Terracart yet
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        // --- 1. Tiered Terracarts (3D Item Models) ---
        itemModelGenerators.itemModelOutput.accept(
                ModItems.STANDARD_TERRACART,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "item/standard_terracart"))
        );
        itemModelGenerators.itemModelOutput.accept(
                ModItems.SWIFT_TERRACART,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "item/swift_terracart"))
        );
        itemModelGenerators.itemModelOutput.accept(
                ModItems.STURDY_TERRACART,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "item/sturdy_terracart"))
        );
        itemModelGenerators.itemModelOutput.accept(
                ModItems.PRIME_TERRACART,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "item/prime_terracart"))
        );

        // --- 2. Tiered Terracart Cores (3D Item Models) ---
        itemModelGenerators.itemModelOutput.accept(
                ModItems.STANDARD_TERRACART_CORE,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "item/standard_terracart_core"))
        );
        itemModelGenerators.itemModelOutput.accept(
                ModItems.SWIFT_TERRACART_CORE,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "item/swift_terracart_core"))
        );
        itemModelGenerators.itemModelOutput.accept(
                ModItems.STURDY_TERRACART_CORE,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "item/sturdy_terracart_core"))
        );
        itemModelGenerators.itemModelOutput.accept(
                ModItems.PRIME_TERRACART_CORE,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "item/prime_terracart_core"))
        );

        // --- 3. Components (Wheel 3D Item Model) ---
        itemModelGenerators.itemModelOutput.accept(
                ModItems.TERRRACART_WHEEL,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "item/terracart_wheel"))
        );
    }
}
