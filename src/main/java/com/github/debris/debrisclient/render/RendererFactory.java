package com.github.debris.debrisclient.render;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class RendererFactory {
    public static Renderer text(Component text, BlockPos pos) {
        return Renderer.of();// TODO
    }

    public static Renderer text(Component text, Entity entity) {
        return Renderer.of();
    }
}
