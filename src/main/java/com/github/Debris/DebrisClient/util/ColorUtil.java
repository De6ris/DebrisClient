package com.github.Debris.DebrisClient.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Formatting;

import java.util.EnumMap;

public class ColorUtil {
    private static final EnumMap<SpawnGroup, Formatting> SPAWN_GROUP_FORMATTING_MAP = new EnumMap<>(SpawnGroup.class);

    public static Formatting getColor(EntityType<?> entityType) {
        return SPAWN_GROUP_FORMATTING_MAP.getOrDefault(entityType.getSpawnGroup(), Formatting.WHITE);
    }

    static {
        SPAWN_GROUP_FORMATTING_MAP.put(SpawnGroup.MONSTER, Formatting.RED);
        SPAWN_GROUP_FORMATTING_MAP.put(SpawnGroup.CREATURE, Formatting.GREEN);
        SPAWN_GROUP_FORMATTING_MAP.put(SpawnGroup.AMBIENT, Formatting.BLACK);
        SPAWN_GROUP_FORMATTING_MAP.put(SpawnGroup.AXOLOTLS, Formatting.GOLD);
        SPAWN_GROUP_FORMATTING_MAP.put(SpawnGroup.UNDERGROUND_WATER_CREATURE, Formatting.DARK_BLUE);
        SPAWN_GROUP_FORMATTING_MAP.put(SpawnGroup.WATER_CREATURE, Formatting.AQUA);
        SPAWN_GROUP_FORMATTING_MAP.put(SpawnGroup.WATER_AMBIENT, Formatting.DARK_AQUA);
        SPAWN_GROUP_FORMATTING_MAP.put(SpawnGroup.MISC, Formatting.YELLOW);
    }
}
