package com.github.debris.debrisclient.feat;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PlayerRotation {
    /**
     * This look at top center.
     */
    public static void lookAtBlockPos(LocalPlayer player, BlockPos pos) {
        lookAtDirection(player, Vec3.atLowerCornerWithOffset(pos, 0.5, 1, 0.5).subtract(player.getEyePosition()));
    }

    public static void lookAtEntity(LocalPlayer player, Entity entity) {
        lookAtDirection(player, entity.getEyePosition().subtract(player.getEyePosition()));
    }

    public static void lookAtDirection(LocalPlayer player, Vec3 directionVec) {
        double dx = directionVec.x;
        double dy = directionVec.y;
        double dz = directionVec.z;
        double dh = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dh));
        player.snapTo(player.getX(), player.getY(), player.getZ(), yaw, pitch);
    }
}
