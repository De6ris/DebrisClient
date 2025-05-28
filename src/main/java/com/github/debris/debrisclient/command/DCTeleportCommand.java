package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.unsafe.tweakeroo.TweakerooAccessor;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.xpple.clientarguments.arguments.CCoordinates;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import dev.xpple.clientarguments.arguments.CVec3Argument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCTeleportCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> literalCommandNode = dispatcher.register(literal(Commands.PREFIX + "teleport")
                .requires(source -> ModReference.hasMod(ModReference.Tweakeroo))
                .then(argument("location", CVec3Argument.vec3())
                        .executes(
                                ctx -> execute(
                                        ctx.getSource(), CVec3Argument.getPosArgument(ctx, "location")
                                )
                        )
                )
                .then(argument("destination", CEntityArgument.entity())
                        .executes(
                                ctx -> execute(
                                        ctx.getSource(), CEntityArgument.getEntity(ctx, "destination")
                                )
                        )
                )
        );

        dispatcher.register(literal(Commands.PREFIX + "tp").redirect(literalCommandNode));
    }

    private static int execute(FabricClientCommandSource source, Entity destination) {
        teleport(source, destination.getPos(), destination.getYaw(), destination.getPitch());
        return Command.SINGLE_SUCCESS;
    }

    private static int execute(FabricClientCommandSource source, CCoordinates location) {
        Entity entity = source.getEntity();
        teleport(source, location.getPosition(source), entity.getYaw(), entity.getPitch());
        return Command.SINGLE_SUCCESS;
    }

    private static void teleport(FabricClientCommandSource source, Vec3d pos, float yaw, float pitch) {
        TweakerooAccessor.tryActivateFreeCam();
        ClientPlayerEntity entity = TweakerooAccessor.getCamEntity();
        entity.refreshPositionAndAngles(pos, yaw, pitch);
    }
}
