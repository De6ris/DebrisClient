package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.command.Commands;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.permissions.Permissions;

public class TeleportUtil {
    public static String suggestCommand(Minecraft client, BlockPos pos) {
        String name = suggestCommand(client);
        return String.format("/%s %d %d %d", name, pos.getX(), pos.getY(), pos.getZ());
    }

    private static String suggestCommand(Minecraft client) {
        return canUseDefaultCommand(client) ? "tp" : Commands.PREFIX + "tp";
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean canUseDefaultCommand(Minecraft client) {
        return client.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }
}
