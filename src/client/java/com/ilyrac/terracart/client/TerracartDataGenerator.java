package com.ilyrac.terracart.client;

import com.ilyrac.terracart.client.datagen.TerracartModelGenerator;
import com.ilyrac.terracart.client.datagen.TerracartRecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jspecify.annotations.NonNull;

public class TerracartDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(@NonNull FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(TerracartRecipeGenerator::new);
        pack.addProvider(TerracartModelGenerator::new);
    }
}
