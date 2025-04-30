package com.github.Debris.DebrisClient.fabric;

import com.github.Debris.DebrisClient.command.Commands;
import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.listener.InitListener;
import com.github.Debris.DebrisClient.util.Predicates;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ModImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitListener());

        if (Predicates.hasMod(ModReference.ClientArguments)) {
            ClientCommandRegistrationCallback.EVENT.register(Commands::register);
        }
    }
}
