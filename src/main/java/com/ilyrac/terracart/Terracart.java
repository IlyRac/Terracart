package com.ilyrac.terracart;

import com.ilyrac.terracart.component.ModDataComponents;
import com.ilyrac.terracart.entity.ModEntities;
import com.ilyrac.terracart.item.ModItems;
import com.ilyrac.terracart.loot.ModLootTableModifiers;
import com.ilyrac.terracart.network.TerracartInputPayload;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Terracart implements ModInitializer {
	public static final String MOD_ID = "terracart";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModDataComponents.initialize();
		TerracartInputPayload.initialize();
		ModItems.initialize();
		ModEntities.initialize();
		ModSounds.initialize();
		ModLootTableModifiers.modifyLootTables();

		LOGGER.info("Terracart Loaded Successfully!");
	}
}