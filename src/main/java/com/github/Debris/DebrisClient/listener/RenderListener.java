package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.render.ComparatorRenderer;
import com.github.Debris.DebrisClient.render.PathNodesRenderer;
import com.github.Debris.DebrisClient.render.RenderContext;
import com.github.Debris.DebrisClient.render.WorldEditRenderer;
import com.github.Debris.DebrisClient.util.Predicates;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

public class RenderListener implements IRenderer {
    private final static RenderListener Instance = new RenderListener();

    public static RenderListener getInstance() {
        return Instance;
    }

    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onRenderWorldLastAdvanced(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, BufferBuilderStorage buffers, Profiler profiler) {
        if (Predicates.notInGame(this.client)) return;

        if (DCCommonConfig.WorldEditVisibility.getBooleanValue() && FabricLoader.getInstance().isModLoaded(ModReference.WorldEdit)) {
            WorldEditRenderer.getInstance().render(posMatrix);
        }

        ComparatorRenderer.onRenderWorldLast(this.client);

        float tickDelta = this.client.getRenderTickCounter().getTickProgress(false);
        PathNodesRenderer.getInstance().onRenderWorldPost(this.client.world, RenderContext.ofWorld(fb, posMatrix, projMatrix, frustum, camera, fog, buffers, profiler, tickDelta));
    }
}
