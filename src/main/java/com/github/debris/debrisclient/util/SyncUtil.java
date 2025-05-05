package com.github.debris.debrisclient.util;

import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;

public class SyncUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Entity syncEntityDataFromIntegratedServer(Entity entity) {
        IntegratedServer server = MinecraftClient.getInstance().getServer();
        if (server == null) {
            return entity;
        }

        ServerWorld serverWorld = server.getWorld(entity.getWorld().getRegistryKey());
        if (serverWorld == null) {
            LOGGER.warn("no world {} on server?", entity.getWorld().getRegistryKey());
            return entity;
        }

        Entity localEntity = serverWorld.getEntity(entity.getUuid());
        if (localEntity == null) {
            LOGGER.warn("no entity with uuid {} on server?", entity.getUuid());
            return entity;
        }

        return localEntity;
    }
}
