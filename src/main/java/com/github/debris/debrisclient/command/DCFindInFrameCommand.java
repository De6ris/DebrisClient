package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.util.ChatUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemInput;

import static dev.xpple.clientarguments.arguments.CItemArgument.getItemStackArgument;
import static dev.xpple.clientarguments.arguments.CItemArgument.itemStack;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCFindInFrameCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal(Commands.PREFIX + "find_in_frame")
                .then(argument("item", itemStack(context))
                        .executes(ctx -> find(ctx.getSource(), getItemStackArgument(ctx, "item")))
                )
        );
    }

    private static int find(FabricClientCommandSource source, ItemInput itemInput) {
        String id = itemInput.getItem().toString();
        String command = String.format("/cfind @e[type=minecraft:item_frame,nbt={Item:{id:\"%s\"}}]", id);
        ChatUtil.sendChat(source.getClient(), command);
        return Command.SINGLE_SUCCESS;
    }
}
