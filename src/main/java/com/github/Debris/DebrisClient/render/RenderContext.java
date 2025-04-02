package com.github.Debris.DebrisClient.render;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

public interface RenderContext {
    float getTickDelta();


    static RenderContext ofEntity(float yaw, float tickDelta, MatrixStack matrices,
                                  VertexConsumerProvider vertexConsumers,
                                  int light) {
        return new EntityRenderContext(yaw, tickDelta, matrices, vertexConsumers, light);
    }

    static RenderContext ofWorld(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, BufferBuilderStorage buffers, Profiler profiler, float tickDelta) {
        return new WorldRenderContext(fb, posMatrix, projMatrix, frustum, camera, fog, buffers, profiler, tickDelta);
    }


    record EntityRenderContext(float yaw, float tickDelta, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers,
                               int light) implements RenderContext {
        @Override
        public float getTickDelta() {
            return this.tickDelta;
        }
    }

    record WorldRenderContext(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera,
                              Fog fog, BufferBuilderStorage buffers, Profiler profiler,
                              float tickDelta) implements RenderContext {
        @Override
        public float getTickDelta() {
            return this.tickDelta;
        }
    }
}
