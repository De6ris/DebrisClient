package com.github.Debris.DebrisClient.unsafe.tweakeroo;

import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Optional;

public class RayTraceUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<HitResult> getPlayerRayTrace(MinecraftClient client) {
        World world = WorldUtils.getBestWorld(client);

        if (world == null) {
            LOGGER.warn("why world is null");
            return Optional.empty();
        }

        Optional<Entity> cameraEntity = RayTraceUtil.getBestCameraEntity(client, world);

        if (cameraEntity.isEmpty()) return Optional.empty();

        // why not use client.targetedEntity? for compatible with tweakeroo free cam
        HitResult trace = RayTraceUtil.getRayTraceFromEntity(world, cameraEntity.get(), false);
        return Optional.of(trace);
    }

    public static Optional<Entity> getBestCameraEntity(MinecraftClient client, World world) {
        Entity cameraEntity = EntityUtils.getCameraEntity();

        if (cameraEntity == null) {
            LOGGER.warn("why camera is null");
            return Optional.empty();
        }

        if (cameraEntity == client.player && world instanceof ServerWorld) {
            // We need to get the player from the server world (if available, ie. in single player),
            // so that the player itself won't be included in the ray trace
            Entity serverPlayer = world.getPlayerByUuid(client.player.getUuid());

            if (serverPlayer != null) {
                cameraEntity = serverPlayer;
            }
        }

        return Optional.of(cameraEntity);
    }

    @NotNull
    public static HitResult getRayTraceFromEntity(World worldIn, Entity entityIn, boolean useLiquids) {
        return RayTraceUtils.getRayTraceFromEntity(worldIn, entityIn, useLiquids);
    }
}
