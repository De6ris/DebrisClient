package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.compat.ModReference;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

public class Commands {
    public static final String PREFIX = "dc";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        DCWhereIsItCommand.register(dispatcher, context);
        DCCountEntityCommand.register(dispatcher);
        DCDataGetCommand.register(dispatcher);
        DCDebugCommand.register(dispatcher);
        DCSpectateCommand.register(dispatcher);
        DCHeadCommand.register(dispatcher);
        DCListCommand.register(dispatcher);
        DCInteractCommand.register(dispatcher);
        DCTeleportCommand.register(dispatcher);
        DCCommandMacroCommand.register(dispatcher);
        DCReloadCommand.register(dispatcher);
        DCLogCommand.register(dispatcher);

        if (ModReference.hasMod(ModReference.ClientCommands)) {
            DCFindInFrameCommand.register(dispatcher, context);
        }
    }
}
