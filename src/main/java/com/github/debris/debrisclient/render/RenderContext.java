package com.github.debris.debrisclient.render;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

public interface RenderContext {
    float getTickDelta();

    static EntityRenderContext ofEntity(float yaw,
                                        float tickDelta,
                                        MatrixStack matrices,
                                        VertexConsumerProvider vertexConsumers,
                                        int light) {
        return new EntityRenderContext(yaw, tickDelta, matrices, vertexConsumers, light);
    }

    static WorldRenderContext ofWorld(Framebuffer fb,
                                      Matrix4f posMatrix,
                                      Matrix4f projMatrix,
                                      Frustum frustum,
                                      Camera camera,
                                      Fog fog,
                                      BufferBuilderStorage buffers,
                                      Profiler profiler,
                                      float tickDelta) {
        return new WorldRenderContext(fb, posMatrix, projMatrix, frustum, camera, fog, buffers, profiler, tickDelta);
    }

}
