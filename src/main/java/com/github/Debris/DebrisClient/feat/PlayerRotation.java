package com.github.Debris.DebrisClient.feat;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlayerRotation {
    /**
     * This look at top center.
     */
    public static void lookAtBlockPos(ClientPlayerEntity player, BlockPos pos) {
        lookAtDirection(player, Vec3d.add(pos, 0.5, 1, 0.5).subtract(player.getEyePos()));
    }

    public static void lookAtEntity(ClientPlayerEntity player, Entity entity) {
        lookAtDirection(player, entity.getEyePos().subtract(player.getEyePos()));
    }

    public static void lookAtDirection(ClientPlayerEntity player, Vec3d directionVec) {
        double dx = directionVec.x;
        double dy = directionVec.y;
        double dz = directionVec.z;
        double dh = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dh));
        player.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), yaw, pitch);
    }
}
