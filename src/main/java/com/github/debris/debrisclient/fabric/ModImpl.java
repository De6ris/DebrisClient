package com.github.debris.debrisclient.fabric;

import com.github.debris.debrisclient.command.Commands;
import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.listener.ChunkLoadListener;
import com.github.debris.debrisclient.listener.InitListener;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;

public class ModImpl implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitListener());

        if (ModReference.hasMod(ModReference.ClientArguments)) {
            ClientCommandRegistrationCallback.EVENT.register(Commands::register);
        }

        ClientChunkEvents.CHUNK_LOAD.register(ChunkLoadListener::onChunkLoad);
        ClientChunkEvents.CHUNK_UNLOAD.register(ChunkLoadListener::onChunkUnload);
    }
}
