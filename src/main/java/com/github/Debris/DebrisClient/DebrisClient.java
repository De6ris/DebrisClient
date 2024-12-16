package com.github.Debris.DebrisClient;

import com.github.Debris.DebrisClient.listener.InitListener;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

import java.io.File;

public class DebrisClient implements ClientModInitializer {

    public static final String MOD_ID = "DebrisClient";

    public static final String CONFIG_DIR = "./config" + File.separator + MOD_ID + File.separator;

    public static final Logger logger = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitListener());
    }
}
