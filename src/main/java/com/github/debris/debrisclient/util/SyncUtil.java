package com.github.debris.debrisclient.util;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class SyncUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Entity syncEntityDataFromIntegratedServer(Entity entity) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return entity;
        }

        ServerLevel serverWorld = server.getLevel(entity.level().dimension());
        if (serverWorld == null) {
            LOGGER.warn("no world {} on server?", entity.level().dimension());
            return entity;
        }

        Entity localEntity = serverWorld.getEntity(entity.getUUID());
        if (localEntity == null) {
            LOGGER.warn("no entity with uuid {} on server?", entity.getUUID());
            return entity;
        }

        return localEntity;
    }
}
