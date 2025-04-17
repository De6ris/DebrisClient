package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.render.RenderQueue;
import com.github.Debris.DebrisClient.render.RendererFactory;
import com.github.Debris.DebrisClient.util.InteractionUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class EntityInteractor {
    private static final Collection<Entity> TARGETS = new HashSet<>();

    public static boolean running() {
        return !TARGETS.isEmpty();
    }

    public static int size() {
        return TARGETS.size();
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
        TARGETS.removeIf(Entity::isRemoved);
        Optional<Entity> optional = TARGETS.stream().filter(entity -> InteractionUtil.withinReach(client, entity)).findFirst();
        if (optional.isPresent()) {
            Entity entity = optional.get();
            InteractionUtil.interactEntity(client, entity);
            if (Predicates.hasMod(ModReference.MagicLibMCApi)) {
                RenderQueue.add(RendererFactory.text(Text.literal("已交互"), entity), 100);
            }
            TARGETS.remove(entity);
        }
    }
}
