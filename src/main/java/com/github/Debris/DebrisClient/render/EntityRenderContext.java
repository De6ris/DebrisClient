package com.github.Debris.DebrisClient.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public record EntityRenderContext(float yaw, float tickDelta, MatrixStack matrices,
                                  VertexConsumerProvider vertexConsumers,
                                  int light) implements RenderContext {
    @Override
    public float getTickDelta() {
        return this.tickDelta;
    }
}
