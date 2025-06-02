package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.feat.interactor.BlockInteractor;
import com.github.debris.debrisclient.feat.interactor.EntityInteractor;
import com.github.debris.debrisclient.feat.interactor.InteractionFactory;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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
                                        addBlockTask(ctx.getSource(), CEnumArgument.getEnum(ctx, "predicate")))
                        )
                )
                .then(literal("entity")
                        .then(literal("clear")
                                .executes(ctx -> {
                                    EntityInteractor.INSTANCE.clear();
                                    InfoUtils.printActionbarMessage("交互选区内实体: 已停止");
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .executes(ctx -> addEntityTask(ctx.getSource()))
                )
        );
    }

    private static int addBlockTask(FabricClientCommandSource source, InteractionFactory.BlockPredicate predicate) {
        InteractionFactory.addBlockTask(source.getClient(), predicate, false);
        return Command.SINGLE_SUCCESS;
    }

    private static int addEntityTask(FabricClientCommandSource source) {
        InteractionFactory.addEntityTask(source.getClient(), false);
        return Command.SINGLE_SUCCESS;
    }
}
