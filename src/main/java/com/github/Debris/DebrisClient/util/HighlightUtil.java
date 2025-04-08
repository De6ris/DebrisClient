package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import net.minecraft.entity.EntityType;

public class HighlightUtil {
    public static boolean shouldHighlightEntity(EntityType<?> type) {
        if (DCCommonConfig.HighlightAll.getBooleanValue()) return true;

        if (DCCommonConfig.HighlightBlaze.getBooleanValue() && type == EntityType.BLAZE) return true;
        if (DCCommonConfig.HighlightCreeper.getBooleanValue() && type == EntityType.CREEPER) return true;
        if (DCCommonConfig.HighlightEnderman.getBooleanValue() && type == EntityType.ENDERMAN) return true;
        if (DCCommonConfig.HighlightItem.getBooleanValue() && type == EntityType.ITEM) return true;
        if (DCCommonConfig.HighlightPiglinBrute.getBooleanValue() && type == EntityType.PIGLIN_BRUTE) return true;
        if (DCCommonConfig.HighlightWanderingTrader.getBooleanValue() && type == EntityType.WANDERING_TRADER)
            return true;
        if (DCCommonConfig.HighlightWitherSkeleton.getBooleanValue() && type == EntityType.WITHER_SKELETON) return true;

        return DCCommonConfig.HighlightEntityList.getStrings().contains(EntityType.getId(type).toString());
    }
}
