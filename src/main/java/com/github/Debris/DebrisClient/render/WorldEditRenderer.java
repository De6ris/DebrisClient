package com.github.Debris.DebrisClient.render;

import com.github.Debris.DebrisClient.unsafe.worldEdit.WorldEditRegionAccessor;
import com.github.Debris.DebrisClient.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;

public class WorldEditRenderer {
    private static final WorldEditRenderer Instance = new WorldEditRenderer();

    public static WorldEditRenderer getInstance() {
        return Instance;
    }

    private final MinecraftClient client = MinecraftClient.getInstance();

    @SuppressWarnings("ConstantConditions")
    public void render(Matrix4f matrix4f) {
        WorldEditRegionAccessor.getRegion(this.client.player.getNameForScoreboard())
                .ifPresent(x -> RenderUtil.renderWorldEditSelectionBox(x.getLeft(), x.getRight(), matrix4f, this.client));
    }
}
