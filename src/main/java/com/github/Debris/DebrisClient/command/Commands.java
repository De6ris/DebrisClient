package com.github.Debris.DebrisClient.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;

public class Commands {
    public static final String PREFIX = "dc";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess context) {
        DCAutoRepeatCommand.register(dispatcher);
        DCWhereIsItCommand.register(dispatcher, context);
        DCCountEntityCommand.register(dispatcher);
        DCDataGetCommand.register(dispatcher);

        if (FabricLoader.getInstance().isModLoaded("clientcommands")) {
            DCFindInFrameCommand.register(dispatcher, context);
        }
    }
}
