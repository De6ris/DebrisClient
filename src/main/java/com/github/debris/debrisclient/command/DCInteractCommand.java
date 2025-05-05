package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.feat.interactor.BlockInteractor;
import com.github.debris.debrisclient.feat.interactor.EntityInteractor;
import com.github.debris.debrisclient.feat.interactor.InteractionFactory;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.xpple.clientarguments.arguments.CEnumArgument;
import fi.dy.masa.malilib.util.InfoUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCInteractCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "interact")
                .then(literal("block")
                        .then(literal("clear")
                                .executes(ctx -> {
                                    BlockInteractor.INSTANCE.clear();
                                    InfoUtils.printActionbarMessage("交互选区内方块: 已停止");
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(argument("predicate", CEnumArgument.enumArg(InteractionFactory.BlockPredicate.class))
                                .executes(ctx ->
                                        addBlockTask(ctx.getSource(), CEnumArgument.getEnum(ctx, "predicate"), 1))
                                .then(argument("times", IntegerArgumentType.integer(1))
                                        .executes(ctx ->
                                                addBlockTask(ctx.getSource(), CEnumArgument.getEnum(ctx, "predicate"), IntegerArgumentType.getInteger(ctx, "times"))))
                        )
                )
                .then(literal("entity")
                        .then(literal("clear")
                                .executes(ctx -> {
                                    EntityInteractor.INSTANCE.clear();
                                    InfoUtils.printActionbarMessage("交互选区内实体: 已停止");
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .executes(ctx -> addEntityTask(ctx.getSource(), 1))
                        .then(argument("times", IntegerArgumentType.integer(1))
                                .executes(ctx -> addEntityTask(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "times"))))
                )
        );
    }

    private static int addBlockTask(FabricClientCommandSource source, InteractionFactory.BlockPredicate predicate, int times) {
        for (int i = 0; i < times; i++) {
            InteractionFactory.addBlockTask(source.getClient(), predicate, false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addEntityTask(FabricClientCommandSource source, int times) {
        for (int i = 0; i < times; i++) {
            InteractionFactory.addEntityTask(source.getClient(), false);
        }
        return Command.SINGLE_SUCCESS;
    }
}
