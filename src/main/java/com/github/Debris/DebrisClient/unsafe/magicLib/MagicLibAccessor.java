package com.github.Debris.DebrisClient.unsafe.magicLib;

import com.github.Debris.DebrisClient.render.Renderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import top.hendrixshen.magiclib.impl.render.TextRenderer;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;

public class MagicLibAccessor {
    public static void renderText(Object o, BlockPos pos) {
        TextRenderer.create()
                .text(ComponentUtil.simple(o)
                        .formatted(Formatting.GREEN))
                .atCenter(pos)
                .seeThrough()
                .render();
    }

    public static Renderer createTextRenderer(Text text, BlockPos pos) {
        TextRenderer textRenderer = TextRenderer.create()
                .text(text)
                .atCenter(pos)
                .seeThrough();
        return context -> textRenderer.render();
    }

    public static Renderer createTextRenderer(Text text, Entity entity) {
        TextRenderer textRenderer = TextRenderer.create()
                .text(text)
                .seeThrough();
        return context -> textRenderer.at(entity.getPos()).render();
    }
}
