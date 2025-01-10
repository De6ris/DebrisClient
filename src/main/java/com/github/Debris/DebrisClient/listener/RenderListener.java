package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.render.ComparatorRenderer;
import com.github.Debris.DebrisClient.render.PathNodesRenderer;
import com.github.Debris.DebrisClient.render.RenderContext;
import com.github.Debris.DebrisClient.render.WorldEditRenderer;
import com.github.Debris.DebrisClient.util.Predicates;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;

public class RenderListener implements IRenderer {
    private final static RenderListener Instance = new RenderListener();

    public static RenderListener getInstance() {
        return Instance;
    }

    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onRenderWorldLast(Matrix4f matrix4f, Matrix4f projMatrix) {
        if (Predicates.notInGame(this.client)) return;

        if (DCCommonConfig.WorldEditVisibility.getBooleanValue() && FabricLoader.getInstance().isModLoaded("worldedit")) {
            WorldEditRenderer.getInstance().render(matrix4f);
        }

        ComparatorRenderer.onRenderWorldLast(this.client);

        PathNodesRenderer.getInstance().onRenderWorldPost(this.client.world, RenderContext.ofWorld(matrix4f, projMatrix), this.client.getRenderTickCounter().getTickDelta(false));
    }
}
