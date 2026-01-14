package com.github.debris.debrisclient.datagen;

import com.github.debris.debrisclient.DebrisClient;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import java.nio.file.Path;

public class DCDataGen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        Path lang = Path.of("")
                .toAbsolutePath()//datagen
                .getParent()//build
                .getParent()//root
                .resolve("src")
                .resolve("main")
                .resolve("resources")
                .resolve("assets")
                .resolve(DebrisClient.MOD_ID)
                .resolve("lang");

    }
}
