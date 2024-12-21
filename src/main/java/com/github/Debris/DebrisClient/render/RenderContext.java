package com.github.Debris.DebrisClient.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public interface RenderContext {
    static RenderContext ofEntity(float yaw, float tickDelta, MatrixStack matrices,
                                  VertexConsumerProvider vertexConsumers,
                                  int light) {
        return new EntityRenderContext(yaw, tickDelta, matrices, vertexConsumers, light);
    }

    record EntityRenderContext(float yaw, float tickDelta, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers,
                               int light) implements RenderContext {
    }


    static RenderContext ofWorld(Matrix4f matrix4f, Matrix4f matrix4f2) {
        return new WorldRenderContext(matrix4f, matrix4f2);
    }

    record WorldRenderContext(Matrix4f matrix4f, Matrix4f matrix4f2) implements RenderContext {
    }
}
