package com.ilyrac.terracart;

import com.ilyrac.terracart.entity.ModEntities;
import com.ilyrac.terracart.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Terracart implements ModInitializer {
	public static final String MOD_ID = "terracart";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		ModEntities.initialize();
		ModSounds.initialize();
		LOGGER.info("Terracart Loaded! Successfully!");
	}
}