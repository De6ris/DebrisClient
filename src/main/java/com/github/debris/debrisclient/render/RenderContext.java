package com.github.debris.debrisclient.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;

public interface RenderContext {
    float getTickDelta();

    static EntityRenderContext ofEntity(float yaw,
                                        float tickDelta) {
        return new EntityRenderContext(yaw, tickDelta);
    }

    static WorldRenderContext ofWorld(RenderTarget fb,
                                      Matrix4f posMatrix,
                                      Matrix4f projMatrix,
                                      Frustum frustum,
                                      Camera camera,
                                      RenderBuffers buffers,
                                      ProfilerFiller profiler,
                                      float tickDelta) {
        return new WorldRenderContext(fb, posMatrix, projMatrix, frustum, camera, buffers, profiler, tickDelta);
    }

}
