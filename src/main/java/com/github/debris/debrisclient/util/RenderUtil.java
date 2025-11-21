package com.github.debris.debrisclient.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RenderUtil {
    @SuppressWarnings("ConstantConditions")
    public static void drawConnectLine(Vec3 pos1, Vec3 pos2, double boxLength, Color4f pos1Color, Color4f pos2Color, @NotNull Color4f lineColor) {
        RenderUtils.depthTest(false);

        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        // do not use EntityUtils.getCameraEntity().getPos(), that is not real camera position

        pos1 = pos1.subtract(camPos);
        pos2 = pos2.subtract(camPos);

        RenderContext ctx = new RenderContext(() -> "debrisclient:connect_lines", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL);
        BufferBuilder builder = ctx.getBuilder();

        RenderUtils.drawBoxAllEdgesBatchedLines(
                (float) (pos1.x() - boxLength),
                (float) (pos1.y() - boxLength),
                (float) (pos1.z() - boxLength),
                (float) (pos1.x() + boxLength),
                (float) (pos1.y() + boxLength),
                (float) (pos1.z() + boxLength),
                pos1Color,
                builder
        );

        RenderUtils.drawBoxAllEdgesBatchedLines(
                (float) (pos2.x() - boxLength),
                (float) (pos2.y() - boxLength),
                (float) (pos2.z() - boxLength),
                (float) (pos2.x() + boxLength),
                (float) (pos2.y() + boxLength),
                (float) (pos2.z() + boxLength),
                pos2Color,
                builder
        );

        builder.addVertex((float) pos1.x(), (float) pos1.y(), (float) pos1.z()).setColor(lineColor.r, lineColor.g, lineColor.b, lineColor.a);
        builder.addVertex((float) pos2.x(), (float) pos2.y(), (float) pos2.z()).setColor(lineColor.r, lineColor.g, lineColor.b, lineColor.a);

        try {
            ctx.draw(builder.build());
            ctx.close();
        } catch (Exception ignored) {
        }

        RenderUtils.depthTest(true);
    }

}
