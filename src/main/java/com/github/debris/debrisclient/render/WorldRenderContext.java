package com.github.debris.debrisclient.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;

public record WorldRenderContext(
        RenderTarget fb,
        CameraRenderState cameraState,
        Frustum culling,
        RenderBuffers buffers,
        ProfilerFiller profiler,
        float tickDelta
) implements RenderContext {
    @Override
    public float getTickDelta() {
        return this.tickDelta;
    }
}
