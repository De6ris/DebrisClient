package com.github.debris.debrisclient.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;

import java.nio.file.Path;
import java.util.Optional;

public class Platform {
    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static boolean hasMod(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static boolean testModVersion(String modId, String versionPredicate) {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modId);
        if (optional.isEmpty()) return false;
        Version version = optional.get().getMetadata().getVersion();
        try {
            return VersionPredicateParser.parse(versionPredicate).test(version);
        } catch (VersionParsingException e) {
            return false;
        }
    }
}
