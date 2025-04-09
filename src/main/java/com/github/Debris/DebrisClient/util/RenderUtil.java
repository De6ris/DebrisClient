package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.unsafe.litematica.LitematicaRenderUtil;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RenderUtil {
    @SuppressWarnings("ConstantConditions")
    public static void drawConnectLine(Vec3d pos1, Vec3d pos2, double boxLength, Color4f pos1Color, Color4f pos2Color, @NotNull Color4f lineColor) {
        RenderUtils.depthTest(false);

        Vec3d camPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        // do not use EntityUtils.getCameraEntity().getPos(), that is not real camera position

        pos1 = pos1.subtract(camPos);
        pos2 = pos2.subtract(camPos);

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.blend(true);

        RenderContext ctx = new RenderContext(MaLiLibPipelines.LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL);
        BufferBuilder builder = ctx.getBuilder();
        MatrixStack matrixStack = new MatrixStack();
        MatrixStack.Entry e = matrixStack.peek();
        RenderUtils.drawBoxAllEdgesBatchedLines(
                (float) (pos1.getX() - boxLength),
                (float) (pos1.getY() - boxLength),
                (float) (pos1.getZ() - boxLength),
                (float) (pos1.getX() + boxLength),
                (float) (pos1.getY() + boxLength),
                (float) (pos1.getZ() + boxLength),
                pos1Color,
                builder,
                e
        );

        RenderUtils.drawBoxAllEdgesBatchedLines(
                (float) (pos2.getX() - boxLength),
                (float) (pos2.getY() - boxLength),
                (float) (pos2.getZ() - boxLength),
                (float) (pos2.getX() + boxLength),
                (float) (pos2.getY() + boxLength),
                (float) (pos2.getZ() + boxLength),
                pos2Color,
                builder,
                e
        );

        builder.vertex((float) pos1.getX(), (float) pos1.getY(), (float) pos1.getZ()).color(lineColor.r, lineColor.g, lineColor.b, lineColor.a).normal(e, 0.0f, 0.0f, 0.0f);
        builder.vertex((float) pos2.getX(), (float) pos2.getY(), (float) pos2.getZ()).color(lineColor.r, lineColor.g, lineColor.b, lineColor.a).normal(e, 0.0f, 0.0f, 0.0f);

        try {
            ctx.draw(builder.endNullable());
            ctx.close();
        } catch (Exception ignored) {
        }

        RenderUtils.depthTest(true);
    }// TODO lines too slim

    public static void renderWorldEditSelectionBox(BlockPos pos1, BlockPos pos2, Matrix4f matrix4f, MinecraftClient client) {
        if (Predicates.hasMod(ModReference.Litematica)) {
            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderUtils.blend(true);

            RenderUtils.depthTest(true);
            RenderUtils.depthMask(false);

            RenderUtils.polygonOffset(true);
            RenderUtils.polygonOffset(-1.2f, -0.2f);

            LitematicaRenderUtil.renderSelectionBox(pos1, pos2, matrix4f);// those set up codes from OverlayRenderer.renderBoxes
            // a yellow outline to differ from the original
            RenderUtils.renderAreaSides(pos1, pos2, DCCommonConfig.WorldEditOverlay.getColor(), matrix4f);

            RenderUtils.polygonOffset(0f, 0f);
            RenderUtils.polygonOffset(false);

            RenderUtils.depthMask(true);
        }
    }

}
