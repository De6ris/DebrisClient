package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.unsafe.tweakeroo.TweakerooAccessor;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.Optional;

public class RayTraceUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<HitResult> getPlayerRayTrace(MinecraftClient client) {
        if (!FabricLoader.getInstance().isModLoaded(ModReference.Tweakeroo)) return Optional.empty();

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

}
