package com.github.debris.debrisclient.fabric;

import com.github.debris.debrisclient.command.Commands;
import com.github.debris.debrisclient.event.Hooks;
import com.github.debris.debrisclient.event.InitListener;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;

public class ModImpl implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitListener());

        ClientCommandRegistrationCallback.EVENT.register(Commands::register);

        ClientChunkEvents.CHUNK_LOAD.register(Hooks::onChunkLoad);
        ClientChunkEvents.CHUNK_UNLOAD.register(Hooks::onChunkUnload);
    }
}
