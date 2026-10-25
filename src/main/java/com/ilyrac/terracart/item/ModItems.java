package com.ilyrac.terracart.item;

import com.ilyrac.terracart.Terracart;
import com.ilyrac.terracart.tier.TerracartTier;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistry;
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

    // --- Core Components ---
    public static final Item TERRRACART_WHEEL = register("terracart_wheel",
            Item::new,
            new Item.Properties().stacksTo(16));

    // --- Tiered Terracarts ---
    public static final TerracartItem STANDARD_TERRACART = register("standard_terracart",
            props -> new TerracartItem(TerracartTier.STANDARD, props),
            new Item.Properties().stacksTo(1));

    public static final TerracartItem SWIFT_TERRACART = register("swift_terracart",
            props -> new TerracartItem(TerracartTier.SWIFT, props),
            new Item.Properties().stacksTo(1));

    public static final TerracartItem STURDY_TERRACART = register("sturdy_terracart",
            props -> new TerracartItem(TerracartTier.STURDY, props),
            new Item.Properties().stacksTo(1));

    public static final TerracartItem PRIME_TERRACART = register("prime_terracart",
            props -> new TerracartItem(TerracartTier.PRIME, props),
            new Item.Properties().stacksTo(1));

    // --- Tiered Terracart Cores ---
    public static final TerracartCoreItem STANDARD_TERRACART_CORE = register("standard_terracart_core",
            props -> new TerracartCoreItem(TerracartTier.STANDARD, props),
            new Item.Properties().stacksTo(1));

    public static final TerracartCoreItem SWIFT_TERRACART_CORE = register("swift_terracart_core",
            props -> new TerracartCoreItem(TerracartTier.SWIFT, props),
            new Item.Properties().stacksTo(1));

    public static final TerracartCoreItem STURDY_TERRACART_CORE = register("sturdy_terracart_core",
            props -> new TerracartCoreItem(TerracartTier.STURDY, props),
            new Item.Properties().stacksTo(1));

    public static final TerracartCoreItem PRIME_TERRACART_CORE = register("prime_terracart_core",
            props -> new TerracartCoreItem(TerracartTier.PRIME, props),
            new Item.Properties().stacksTo(1));

    public static TerracartItem getCartItemFor(TerracartTier tier) {
        return switch (tier) {
            case SWIFT -> SWIFT_TERRACART;
            case STURDY -> STURDY_TERRACART;
            case PRIME -> PRIME_TERRACART;
            case STANDARD -> STANDARD_TERRACART;
        };
    }

    public static TerracartCoreItem getCoreItemFor(TerracartTier tier) {
        return switch (tier) {
            case SWIFT -> SWIFT_TERRACART_CORE;
            case STURDY -> STURDY_TERRACART_CORE;
            case PRIME -> PRIME_TERRACART_CORE;
            case STANDARD -> STANDARD_TERRACART_CORE;
        };
    }

    // --- Legacy Aliases List ---
    private static final String[] LEGACY_COLOR_NAMES = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    // --- Creative Tab & Aliasing Hooks ---
    public static void initialize() {
        // 1. Registry Aliasing: Map old colored carts and legacy base cart to standard_terracart
        Identifier standardCartId = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "standard_terracart");
        FabricRegistry itemRegistry = (FabricRegistry) BuiltInRegistries.ITEM;

        // Legacy uncolored cart
        itemRegistry.addAlias(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "terracart"), standardCartId);

        // Legacy 16 colored carts
        for (String color : LEGACY_COLOR_NAMES) {
            itemRegistry.addAlias(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, color + "_terracart"), standardCartId);
        }

        // 2. Creative Tabs Hooks
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.insertAfter(Items.TNT_MINECART, STANDARD_TERRACART);
            entries.insertAfter(STANDARD_TERRACART, SWIFT_TERRACART);
            entries.insertAfter(SWIFT_TERRACART, STURDY_TERRACART);
            entries.insertAfter(STURDY_TERRACART, PRIME_TERRACART);

            // Cores in Tools & Utilities (uncalibrated base items)
            entries.insertAfter(PRIME_TERRACART, STANDARD_TERRACART_CORE);
            entries.insertAfter(STANDARD_TERRACART_CORE, SWIFT_TERRACART_CORE);
            entries.insertAfter(SWIFT_TERRACART_CORE, STURDY_TERRACART_CORE);
            entries.insertAfter(STURDY_TERRACART_CORE, PRIME_TERRACART_CORE);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.insertAfter(Items.PHANTOM_MEMBRANE, TERRRACART_WHEEL);
            entries.insertAfter(TERRRACART_WHEEL, STANDARD_TERRACART_CORE);
            entries.insertAfter(STANDARD_TERRACART_CORE, SWIFT_TERRACART_CORE);
            entries.insertAfter(SWIFT_TERRACART_CORE, STURDY_TERRACART_CORE);
            entries.insertAfter(STURDY_TERRACART_CORE, PRIME_TERRACART_CORE);
        });
    }
}