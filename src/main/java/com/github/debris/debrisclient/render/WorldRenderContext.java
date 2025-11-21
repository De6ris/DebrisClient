package com.github.debris.debrisclient.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;

public record WorldRenderContext(
        RenderTarget fb,
        Matrix4f posMatrix,
        Matrix4f projMatrix,
        Frustum frustum,
        Camera camera,
        RenderBuffers buffers,
        ProfilerFiller profiler,
        float tickDelta
) implements RenderContext {
    @Override
    public float getTickDelta() {
        return this.tickDelta;
    }
}
