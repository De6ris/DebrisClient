package com.github.debris.debrisclient.render;

@FunctionalInterface
public interface Renderer {
    void render(RenderContext context);

    Renderer EMPTY = context -> {
    };

    static Renderer of() {
        return EMPTY;
    }
}
