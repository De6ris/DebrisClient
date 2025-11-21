package com.github.debris.debrisclient.unsafe;

import com.github.debris.debrisclient.render.Renderer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import top.hendrixshen.magiclib.impl.render.TextRenderer;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;

public class MagicLibAccess {
    public static void renderText(Object o, BlockPos pos) {
        TextRenderer.create()
                .text(ComponentUtil.simple(o)
                        .withStyle(ChatFormatting.GREEN))
                .atCenter(pos)
                .seeThrough()
                .render();
    }

    public static Renderer createTextRenderer(Component text, BlockPos pos) {
        TextRenderer textRenderer = TextRenderer.create()
                .text(text)
                .atCenter(pos)
                .seeThrough();
        return context -> textRenderer.render();
    }

    public static Renderer createTextRenderer(Component text, Entity entity) {
        TextRenderer textRenderer = TextRenderer.create()
                .text(text)
                .seeThrough();
        return context -> textRenderer.at(entity.position()).render();
    }
}
