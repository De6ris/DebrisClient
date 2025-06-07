package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.feat.interactor.BlockInteractor;
import com.github.debris.debrisclient.feat.interactor.EntityInteractor;
import com.github.debris.debrisclient.feat.interactor.InteractionFactory;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import dev.xpple.clientarguments.arguments.CEnumArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.Entity;

import java.util.function.Predicate;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCInteractCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "interact")
                .then(literal("block")
                        .then(literal("clear")
                                .executes(ctx -> {
                                    BlockInteractor.INSTANCE.clearAndInform();
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(argument("predicate", CEnumArgument.enumArg(InteractionFactory.BlockPredicate.class))
                                .executes(ctx ->
                                        addBlockTask(ctx.getSource(), CEnumArgument.getEnum(ctx, "predicate")))
                        )
                )
                .then(literal("entity")
                        .executes(ctx -> addEntityTask(ctx.getSource()))
                        .then(literal("clear")
                                .executes(ctx -> {
                                    EntityInteractor.INSTANCE.clearAndInform();
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(argument("filter", CEntityArgument.entities())
                                .executes(ctx -> addEntityTask(ctx.getSource(), CEntityArgument.getEntities(ctx, "filter")::contains))
                        )
                )
        );
    }


    private static int addBlockTask(FabricClientCommandSource source, InteractionFactory.BlockPredicate predicate) {
        InteractionFactory.addBlockTask(source.getClient(), predicate, false);
        return Command.SINGLE_SUCCESS;
    }

    private static int addEntityTask(FabricClientCommandSource source) {
        return addEntityTask(source, entity -> true);
    }

    private static int addEntityTask(FabricClientCommandSource source, Predicate<Entity> predicate) {
        InteractionFactory.addEntityTask(source.getClient(), predicate, false);
        return Command.SINGLE_SUCCESS;
    }
}
