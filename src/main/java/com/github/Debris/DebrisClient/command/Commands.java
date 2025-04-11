package com.github.Debris.DebrisClient.command;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.util.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class Commands {
    public static final String PREFIX = "dc";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess context) {
        DCWhereIsItCommand.register(dispatcher, context);
        DCCountEntityCommand.register(dispatcher);
        DCDataGetCommand.register(dispatcher);
        DCDebugCommand.register(dispatcher);
        DCSpectateCommand.register(dispatcher);
        DCHeadCommand.register(dispatcher);
        DCListCommand.register(dispatcher);

        if (Predicates.hasMod(ModReference.ClientCommands)) {
            DCFindInFrameCommand.register(dispatcher, context);
        }
    }
}
