package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.unsafe.mgButtons.MGButtonReloader;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCReloadCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(Commands.PREFIX + "reload")
                        .then(literal("command_button")
                                .executes(ctx -> reloadCommandButton(ctx.getSource()))
                        )
        );
    }

    private static int reloadCommandButton(FabricClientCommandSource source) {
        if (ModReference.hasMod(ModReference.CommandButton)) {
            MGButtonReloader.reload();
            InfoUtils.showInGameMessage(Message.MessageType.SUCCESS, "命令按钮: 重载成功");
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}
