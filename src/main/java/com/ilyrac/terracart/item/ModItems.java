package com.ilyrac.terracart.item;

import com.ilyrac.terracart.Terracart;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    // ----- COLORS -----
    private static final String[] COLOR_NAMES = {
            "white","orange","magenta","light_blue","yellow","lime","pink","gray",
            "light_gray","cyan","purple","blue","brown","green","red","black"
    };
    public static final Item[] COLORED_TERRACARTS = new Item[16];

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(
        entries -> {
            entries.addAfter(Items.TNT_MINECART, TERRACART);

            for (Item it : COLORED_TERRACARTS) {
                entries.addAfter(TERRACART, it);
            }
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(
            entries -> entries.addAfter(Items.PHANTOM_MEMBRANE, CART_WHEEL)
        );
    }

    public static final Item CART_WHEEL = register("cart_wheel",
            Item::new,
            new Item.Properties().stacksTo(16));

    public static final Item TERRACART = register("terracart",
            TerracartItem::new,
            new Item.Properties().stacksTo(1));

    static {
        for (int i = 0; i < COLOR_NAMES.length; i++) {
            final int colorId = i;
            String registryName = COLOR_NAMES[i] + "_terracart";
            COLORED_TERRACARTS[i] = register(
                    registryName,
                    props -> new ColoredTerracartItem(colorId, props),
                    new Item.Properties().stacksTo(1)
            );
        }
    }
}