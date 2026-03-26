package com.github.debris.debrisclient.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4fc;

public interface RenderContext {
    float getTickDelta();

    static EntityRenderContext ofEntity(float yaw,
                                        float tickDelta) {
        return new EntityRenderContext(yaw, tickDelta);
    }

    static WorldRenderContext ofWorld(RenderTarget fb,
                                      Matrix4fc modelViewMatrix,
                                      CameraRenderState cameraState,
                                      Frustum culling,
                                      RenderBuffers buffers,
                                      ProfilerFiller profiler,
                                      float tickDelta) {
        return new WorldRenderContext(fb, modelViewMatrix, cameraState, culling, buffers, profiler, tickDelta);
    }

}
