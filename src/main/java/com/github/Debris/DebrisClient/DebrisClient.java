package com.github.Debris.DebrisClient;

import com.github.Debris.DebrisClient.command.Commands;
import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.listener.InitListener;
import com.github.Debris.DebrisClient.util.Predicates;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;

import java.io.File;

public class DebrisClient implements ClientModInitializer {
    public static final String MOD_ID = "debris_client";
    public static final String MOD_NAME = "DebrisClient";
    public static final String MOD_VERSION = "1.0.0";

    public static final String CONFIG_DIR = "./config" + File.separator + MOD_NAME + File.separator;

    public static final Logger logger = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitListener());

        if (Predicates.hasMod(ModReference.ClientArguments)) {
            ClientCommandRegistrationCallback.EVENT.register(Commands::register);
        }
    }
}
