package com.github.debris.debrisclient.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCDebugCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "debug")
                .executes(ctx -> processCommand(ctx.getSource()))
        );
    }

    private static int processCommand(FabricClientCommandSource source) {

        return Command.SINGLE_SUCCESS;
    }

}
