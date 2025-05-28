package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.command.Commands;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class TeleportUtil {
    public static String suggestCommand(MinecraftClient client, BlockPos pos) {
        String name = suggestCommand(client);
        return String.format("/%s %d %d %d", name, pos.getX(), pos.getY(), pos.getZ());
    }

    private static String suggestCommand(MinecraftClient client) {
        return canUseDefaultCommand(client) ? "tp" : Commands.PREFIX + "tp";
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean canUseDefaultCommand(MinecraftClient client) {
        return client.player.hasPermissionLevel(2);
    }
}
