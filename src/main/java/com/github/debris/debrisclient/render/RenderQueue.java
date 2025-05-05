package com.github.debris.debrisclient.render;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class RenderQueue {
    private static int TICK_COUNTER = 0;

    private static final List<RenderEntry> ENTRIES = new ArrayList<>();

    /**
     * @param lifespan in ticks
     */
    public static void add(Renderer renderer, int lifespan) {
        ENTRIES.add(new RenderEntry(renderer, TICK_COUNTER + lifespan));
    }

    public static void onClientTick(MinecraftClient client) {
        ENTRIES.removeIf(entry -> entry.removeTick == TICK_COUNTER);
        TICK_COUNTER++;
    }

    public static void onRenderWorldPost(WorldRenderContext context) {
        ENTRIES.forEach(x -> x.renderer.render(context));
    }

    private record RenderEntry(Renderer renderer, int removeTick) {
    }
}
