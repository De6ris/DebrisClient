package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.feat.ItemBotMapping;
import com.github.debris.debrisclient.localization.GeneralText;
import com.github.debris.debrisclient.unsafe.MGButtonAccess;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCReloadCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(Commands.PREFIX + "reload")
                        .then(literal("command_button")
                                .executes(ctx -> reloadCommandButton(ctx.getSource()))
                        )
                        .then(literal("item_bot_mapping")
                                .executes(ctx -> reloadItemBotMapping(ctx.getSource())))
        );
    }

    private static int reloadCommandButton(FabricClientCommandSource source) {
        if (ModReference.hasMod(ModReference.CommandButton)) {
            MGButtonAccess.reload();
            source.sendFeedback(GeneralText.RELOAD_SUCCESS.translate());
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFeedback(GeneralText.FEATURE_REQUIRES_MOD.translate(ModReference.CommandButton));
            return 0;
        }
    }

    private static int reloadItemBotMapping(FabricClientCommandSource source) {
        Component component = ItemBotMapping.reload();
        if (component == null) {
            source.sendFeedback(GeneralText.RELOAD_SUCCESS.translate());
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFeedback(component);
        }
        return 0;
    }
}
