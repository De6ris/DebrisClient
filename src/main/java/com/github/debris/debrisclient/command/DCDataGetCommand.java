package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.util.ChatUtil;
import com.github.debris.debrisclient.util.RayTraceUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;
import java.util.UUID;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCDataGetCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(Commands.PREFIX + "data_get")
                        .executes(ctx -> execute(ctx.getSource()))
        );
    }

    private static int execute(FabricClientCommandSource source) {
        dataGet(source);
        return Command.SINGLE_SUCCESS;
    }

    private static void dataGet(FabricClientCommandSource source) {
        Minecraft client = source.getClient();
        Optional<HitResult> optional = RayTraceUtil.getPlayerRayTrace(client);
        if (optional.isEmpty()) {
            source.sendFeedback(Component.literal("未指向内容"));
            return;
        }
        HitResult trace = optional.get();
        switch (trace.getType()) {
            case BLOCK -> {
                BlockPos blockPos = ((BlockHitResult) trace).getBlockPos();
                ChatUtil.sendChat(client, String.format("/data get block %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            }
            case ENTITY -> {
                UUID uuid = ((EntityHitResult) trace).getEntity().getUUID();
                ChatUtil.sendChat(client, String.format("/data get entity %s", uuid));
            }
        }
    }
}
