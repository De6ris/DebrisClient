package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.util.InteractionUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class EntityInteractor {
    private static final Collection<Entity> TARGETS = new HashSet<>();

    public static boolean running() {
        return !TARGETS.isEmpty();
    }

    public static void stop() {
        TARGETS.clear();
    }

    public static void add(Entity pos) {
        TARGETS.add(pos);
    }

    public static void addAll(Collection<Entity> list) {
        TARGETS.addAll(list);
    }

    public static void onClientTick(MinecraftClient client) {
        if (!Predicates.inGameNoGui(client)) return;
        if (TARGETS.isEmpty()) return;
        Optional<Entity> optional = TARGETS.stream().filter(entity -> InteractionUtil.withinReach(client, entity)).findFirst();
        if (optional.isPresent()) {
            Entity entity = optional.get();
            InteractionUtil.interactEntity(client, entity);
            TARGETS.remove(entity);
            InfoUtils.printActionbarMessage("实体交互: 还剩" + TARGETS.size() + "处");
        }
    }
}
