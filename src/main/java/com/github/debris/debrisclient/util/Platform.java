package com.github.debris.debrisclient.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

import java.nio.file.Path;
import java.util.Optional;

public class Platform {
    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static boolean hasMod(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static boolean isModLoadedWithNewEnoughVersion(String modId, String leastVersion) {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modId);
        if (optional.isEmpty()) return false;
        Version version = optional.get().getMetadata().getVersion();
        try {
            Version parse = Version.parse(leastVersion);
            if (version.compareTo(parse) >= 0) return true;
        } catch (VersionParsingException e) {
            return false;
        }
        return false;
    }
}
