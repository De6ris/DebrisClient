package com.github.debris.debrisclient.util;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.EnumMap;

public class ColorUtil {
    private static final EnumMap<MobCategory, ChatFormatting> SPAWN_GROUP_FORMATTING_MAP = new EnumMap<>(MobCategory.class);

    public static ChatFormatting getColor(EntityType<?> entityType) {
        return SPAWN_GROUP_FORMATTING_MAP.getOrDefault(entityType.getCategory(), ChatFormatting.WHITE);
    }

    static {
        SPAWN_GROUP_FORMATTING_MAP.put(MobCategory.MONSTER, ChatFormatting.RED);
        SPAWN_GROUP_FORMATTING_MAP.put(MobCategory.CREATURE, ChatFormatting.GREEN);
        SPAWN_GROUP_FORMATTING_MAP.put(MobCategory.AMBIENT, ChatFormatting.BLACK);
        SPAWN_GROUP_FORMATTING_MAP.put(MobCategory.AXOLOTLS, ChatFormatting.GOLD);
        SPAWN_GROUP_FORMATTING_MAP.put(MobCategory.UNDERGROUND_WATER_CREATURE, ChatFormatting.DARK_BLUE);
        SPAWN_GROUP_FORMATTING_MAP.put(MobCategory.WATER_CREATURE, ChatFormatting.AQUA);
        SPAWN_GROUP_FORMATTING_MAP.put(MobCategory.WATER_AMBIENT, ChatFormatting.DARK_AQUA);
        SPAWN_GROUP_FORMATTING_MAP.put(MobCategory.MISC, ChatFormatting.YELLOW);
    }
}
