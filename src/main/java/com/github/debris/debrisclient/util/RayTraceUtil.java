package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.unsafe.tweakeroo.TweakerooAccessor;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.Optional;

public class RayTraceUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<HitResult> getPlayerRayTrace(MinecraftClient client) {
        if (!ModReference.hasMod(ModReference.Tweakeroo)) return Optional.empty();

        World world = WorldUtils.getBestWorld(client);

        if (world == null) {
            LOGGER.warn("why world is null");
            return Optional.empty();
        }

        Optional<Entity> cameraEntity = getBestCameraEntity(client, world);

        if (cameraEntity.isEmpty()) return Optional.empty();

        // why not use client.targetedEntity? for compatible with tweakeroo free cam
        HitResult trace = TweakerooAccessor.getRayTraceFromEntity(world, cameraEntity.get(), false);
        return Optional.of(trace);
    }

    public static Optional<Entity> getBestCameraEntity(MinecraftClient client, World world) {
        Entity cameraEntity = EntityUtils.getCameraEntity();

        if (cameraEntity == null) {
            LOGGER.warn("why camera is null");
            return Optional.empty();
        }

        if (cameraEntity == client.player && world instanceof ServerWorld) {
            // We need to get the player from the server world (if available, i.e. in single player),
            // so that the player itself won't be included in the ray trace
            Entity serverPlayer = world.getPlayerByUuid(client.player.getUuid());

            if (serverPlayer != null) {
                cameraEntity = serverPlayer;
            }
        }

        return Optional.of(cameraEntity);
    }

    public static Optional<BlockPos> getRayTraceBlock(MinecraftClient client) {
        return getPlayerRayTrace(client).map(hitResult -> {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                return ((BlockHitResult) hitResult).getBlockPos();
            }
            return null;
        });
    }

    public static Optional<Entity> getRayTraceEntity(MinecraftClient client) {
        return getPlayerRayTrace(client).map(hitResult -> {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                return ((EntityHitResult) hitResult).getEntity();
            }
            return null;
        });
    }

    @SuppressWarnings("DataFlowIssue")
    public static Optional<BlockEntity> getRayTraceBlockEntity(MinecraftClient client) {
        return getRayTraceBlock(client).map(pos -> {
            ClientWorld world = client.world;
            if (world.getBlockState(pos).hasBlockEntity()) {
                return world.getChunk(pos).getBlockEntity(pos);
            }
            return null;
        });
    }
}
