package com.ilyrac.terracart.item;

import com.ilyrac.terracart.Terracart;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

public class ModItems {

    public static <GenericItem extends Item> GenericItem register(String name, Function<Item.Properties, GenericItem> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Terracart.MOD_ID, name));
        GenericItem item = itemFactory.apply(settings.setId(itemKey));
        return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }

    // --- Core Items ---
    public static final Item TERRRACART_WHEEL = register("terracart_wheel",
            Item::new,
            new Item.Properties().stacksTo(16));

    public static final Item TERRACART = register("terracart",
            TerracartItem::new,
            new Item.Properties().stacksTo(1));

    // --- Colored Variants ---
    private static final String[] COLOR_NAMES = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };
    public static final Item[] COLORED_TERRACARTS = new Item[16];

    static {
        for (int i = 0; i < COLOR_NAMES.length; i++) {
            final int colorId = i;
            String registryName = COLOR_NAMES[i] + "_terracart";
            COLORED_TERRACARTS[i] = register(
                    registryName,
                    props -> new TerracartItem(colorId, props),
                    new Item.Properties().stacksTo(1)
            );
        }
    }

    // --- Creative Tab Hook ---
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.insertAfter(Items.TNT_MINECART, TERRACART);

            // Insert colored items right after the base cart
            Item lastInserted = TERRACART;
            for (Item it : COLORED_TERRACARTS) {
                entries.insertAfter(lastInserted, it);
                lastInserted = it; // Keeps them sequentially ordered in the tab
            }
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(
                entries -> entries.insertAfter(Items.PHANTOM_MEMBRANE, TERRRACART_WHEEL)
        );
    }
}