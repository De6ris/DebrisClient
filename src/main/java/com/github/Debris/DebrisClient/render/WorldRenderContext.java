package com.github.Debris.DebrisClient.render;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

public record WorldRenderContext(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum,
                                 Camera camera,
                                 Fog fog, BufferBuilderStorage buffers, Profiler profiler,
                                 float tickDelta) implements RenderContext {
    @Override
    public float getTickDelta() {
        return this.tickDelta;
    }
}
