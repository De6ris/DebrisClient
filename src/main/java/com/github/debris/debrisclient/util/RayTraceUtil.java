package com.github.debris.debrisclient.util;

import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.game.RayTraceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.slf4j.Logger;

import java.util.Optional;

public class RayTraceUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<HitResult> getPlayerRayTrace(Minecraft client) {
        Level world = WorldUtils.getBestWorld(client);

        if (world == null) {
            LOGGER.warn("why world is null");
            return Optional.empty();
        }

        Optional<Entity> cameraEntity = getBestCameraEntity(client, world);

        if (cameraEntity.isEmpty()) return Optional.empty();

        // why not use client.targetedEntity? for compatible with tweakeroo free cam
        HitResult trace = RayTraceUtils.getRayTraceFromEntity(world, cameraEntity.get(), ClipContext.Fluid.NONE);
        return Optional.ofNullable(trace);
    }

    public static Optional<Entity> getBestCameraEntity(Minecraft client, Level world) {
        Entity cameraEntity = EntityUtils.getCameraEntity();

        if (cameraEntity == null) {
            LOGGER.warn("why camera is null");
            return Optional.empty();
        }

        if (cameraEntity == client.player && world instanceof ServerLevel) {
            // We need to get the player from the server world (if available, i.e. in single player),
            // so that the player itself won't be included in the ray trace
            Entity serverPlayer = world.getPlayerByUUID(client.player.getUUID());

            if (serverPlayer != null) {
                cameraEntity = serverPlayer;
            }
        }

        return Optional.of(cameraEntity);
    }

    public static Optional<BlockPos> getRayTraceBlock(Minecraft client) {
        return getPlayerRayTrace(client).map(hitResult -> {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                return ((BlockHitResult) hitResult).getBlockPos();
            }
            return null;
        });
    }

    public static Optional<Entity> getRayTraceEntity(Minecraft client) {
        return getPlayerRayTrace(client).map(hitResult -> {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                return ((EntityHitResult) hitResult).getEntity();
            }
            return null;
        });
    }

    @SuppressWarnings("DataFlowIssue")
    public static Optional<BlockEntity> getRayTraceBlockEntity(Minecraft client) {
        return getRayTraceBlock(client).map(pos -> {
            ClientLevel world = client.level;
            if (world.getBlockState(pos).hasBlockEntity()) {
                return world.getChunk(pos).getBlockEntity(pos);
            }
            return null;
        });
    }
}
