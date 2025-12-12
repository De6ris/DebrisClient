package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.feat.advancement.AdventuringTimeHelper;
import com.github.debris.debrisclient.localization.AdventureCommandText;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCAdventureCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(Commands.PREFIX + "adventuring_time")
                        .executes(ctx -> toggle(ctx.getSource()))
                        .then(literal("help")
                                .executes(ctx -> help(ctx.getSource())))
        );
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(AdventureCommandText.HELP.translate());
        return Command.SINGLE_SUCCESS;
    }

    private static int toggle(FabricClientCommandSource source) {
        if (!AdventuringTimeHelper.available()) {
            source.sendFeedback(AdventureCommandText.NOT_AVAILABLE.translate());
            source.sendFeedback(AdventureCommandText.SUPPORTED_MODS.translate());
            return 0;
        }

        AdventuringTimeHelper.toggle(source.getClient());
        source.sendFeedback((AdventuringTimeHelper.isActive() ? AdventureCommandText.STATUS_ON : AdventureCommandText.STATUS_OFF).translate());
        return Command.SINGLE_SUCCESS;
    }
}
