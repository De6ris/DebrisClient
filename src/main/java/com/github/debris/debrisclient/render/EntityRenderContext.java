package com.github.debris.debrisclient.render;

public record EntityRenderContext(float yaw, float tickDelta) implements RenderContext {
    @Override
    public float getTickDelta() {
        return this.tickDelta;
    }
}
