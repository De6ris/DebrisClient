package com.github.debris.debrisclient;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.nio.file.Path;

public class DebrisClient {
    public static final String MOD_ID = "debrisclient";
    public static final String MOD_NAME = "DebrisClient";
    public static final String MOD_VERSION = "1.0.0";

    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(DebrisClient.MOD_NAME);

    public static final Logger logger = LogUtils.getLogger();
}
