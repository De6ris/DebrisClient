package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.unsafe.TweakerooAccess;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.xpple.clientarguments.arguments.CCoordinates;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import dev.xpple.clientarguments.arguments.CVec3Argument;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.util.InfoUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

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
        teleport(source, destination.position(), destination.getYRot(), destination.getXRot());
        return Command.SINGLE_SUCCESS;
    }

    private static int execute(FabricClientCommandSource source, CCoordinates location) {
        Entity entity = source.getEntity();
        teleport(source, location.getPosition(source), entity.getYRot(), entity.getXRot());
        return Command.SINGLE_SUCCESS;
    }

    private static void teleport(FabricClientCommandSource source, Vec3 pos, float yaw, float pitch) {
        IConfigBoolean config = TweakerooAccess.getFreeCamConfig();
        if (!config.getBooleanValue()) {
            config.setBooleanValue(true);
            InfoUtils.printBooleanConfigToggleMessage(config.getPrettyName(), true);
        }
        LocalPlayer entity = TweakerooAccess.getCamEntity();
        entity.snapTo(pos, yaw, pitch);
    }
}
