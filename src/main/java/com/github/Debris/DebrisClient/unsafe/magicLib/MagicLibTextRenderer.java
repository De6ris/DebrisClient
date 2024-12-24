package com.github.Debris.DebrisClient.unsafe.magicLib;

import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import top.hendrixshen.magiclib.impl.render.TextRenderer;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;

public class MagicLibTextRenderer {
    public static void renderText(Object o, BlockPos pos) {
        TextRenderer.create()
                .text(ComponentUtil.simple(o)
                        .formatted(Formatting.GREEN))
                .atCenter(pos)
                .seeThrough()
                .render();
    }
}
