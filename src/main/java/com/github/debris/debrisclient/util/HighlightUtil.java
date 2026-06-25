package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

public class HighlightUtil {
    public static boolean shouldHighlightEntity(EntityType<?> type) {
        if (DCCommonConfig.HighlightAll.getBooleanValue()) return true;

        if (DCCommonConfig.HighlightBlaze.getBooleanValue() && type == EntityTypes.BLAZE) return true;
        if (DCCommonConfig.HighlightCreeper.getBooleanValue() && type == EntityTypes.CREEPER) return true;
        if (DCCommonConfig.HighlightEnderman.getBooleanValue() && type == EntityTypes.ENDERMAN) return true;
        if (DCCommonConfig.HighlightItem.getBooleanValue() && type == EntityTypes.ITEM) return true;
        if (DCCommonConfig.HighlightPiglinBrute.getBooleanValue() && type == EntityTypes.PIGLIN_BRUTE) return true;
        if (DCCommonConfig.HighlightPlayer.getBooleanValue() && type == EntityTypes.PLAYER) return true;
        if (DCCommonConfig.HighlightWanderingTrader.getBooleanValue() && type == EntityTypes.WANDERING_TRADER)
            return true;
        if (DCCommonConfig.HighlightWitherSkeleton.getBooleanValue() && type == EntityTypes.WITHER_SKELETON)
            return true;

        return DCCommonConfig.HighlightEntityList.getStrings().contains(EntityType.getKey(type).toString());
    }
}
