package com.github.debris.debrisclient.fabric;

import com.github.debris.debrisclient.command.Commands;
import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.listener.InitListener;
import com.github.debris.debrisclient.util.Predicates;
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
