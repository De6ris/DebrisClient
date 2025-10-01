package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.util.StringUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static dev.xpple.clientarguments.arguments.CItemArgument.getItemStackArgument;
import static dev.xpple.clientarguments.arguments.CItemArgument.itemStack;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCWhereIsItCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess context) {
        dispatcher.register(literal(Commands.PREFIX + "where_is_it")
                .then(
                        literal("add")
                                .then(argument("item", itemStack(context))
                                        .executes(ctx ->
                                                find(ctx.getSource(), getItemStackArgument(ctx, "item")))
                                )
                )
                .then(
                        literal("clear")
                                .executes(ctx -> clear(ctx.getSource()))
                )
                .then(
                        literal("help")
                                .executes(ctx -> help(ctx.getSource()))
                )
                .then(
                        literal("list")
                                .executes(ctx -> list(ctx.getSource()))
                )
        );
    }

    private static int find(FabricClientCommandSource source, ItemStackArgument itemInput) {
        Item item = itemInput.getItem();
        if (FIND_QUEUE.contains(item)) {
            source.sendFeedback(Text.literal(String.format("已在寻找%s!", StringUtil.translateItem(item))));
        } else {
            FIND_QUEUE.add(item);
            source.sendFeedback(Text.literal(String.format("成功将%s加入寻找列表", StringUtil.translateItem(item))));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clear(FabricClientCommandSource source) {
        FIND_QUEUE.clear();
        source.sendFeedback(Text.literal("成功清空寻找列表"));
        return Command.SINGLE_SUCCESS;
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("添加目标后, 当你打开容器时, 会自动丢出目标物品并关闭GUI"));
        return Command.SINGLE_SUCCESS;
    }

    private static int list(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(String.format("正在寻找: %s", StringUtil.translateItemCollection(FIND_QUEUE))));
        return Command.SINGLE_SUCCESS;
    }

    private static final List<Item> FIND_QUEUE = new ArrayList<>();

    public static boolean isActive() {
        return !FIND_QUEUE.isEmpty();
    }

    public static Stream<Item> streamItems() {
        return FIND_QUEUE.stream();
    }

    public static void markFound(Collection<Item> found) {
        FIND_QUEUE.removeAll(found);
    }
}
