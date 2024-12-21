package com.github.Debris.DebrisClient.util;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class RenderUtil {
    @SuppressWarnings("ConstantConditions")
    public static void drawConnectLine(Vec3d pos1, Vec3d pos2, double boxLength, Color4f pos1Color, Color4f pos2Color, @NotNull Color4f lineColor) {
        RenderSystem.disableDepthTest();

        Vec3d camPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        // do not use EntityUtils.getCameraEntity().getPos(), that is not real camera position

        pos1 = pos1.subtract(camPos);
        pos2 = pos2.subtract(camPos);

        Tessellator tesselator = Tessellator.getInstance();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.applyModelViewMatrix();
        // what they do? can not render if commented

        BufferBuilder builder = tesselator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        RenderUtils.drawBoxAllEdgesBatchedLines(
                (float) (pos1.getX() - boxLength),
                (float) (pos1.getY() - boxLength),
                (float) (pos1.getZ() - boxLength),
                (float) (pos1.getX() + boxLength),
                (float) (pos1.getY() + boxLength),
                (float) (pos1.getZ() + boxLength),
                pos1Color,
                builder
        );
        RenderUtil.end(builder);


        builder = tesselator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        RenderUtils.drawBoxAllEdgesBatchedLines(
                (float) (pos2.getX() - boxLength),
                (float) (pos2.getY() - boxLength),
                (float) (pos2.getZ() - boxLength),
                (float) (pos2.getX() + boxLength),
                (float) (pos2.getY() + boxLength),
                (float) (pos2.getZ() + boxLength),
                pos2Color,
                builder
        );
        RenderUtil.end(builder);


        builder = tesselator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        builder.vertex((float) pos1.getX(), (float) pos1.getY(), (float) pos1.getZ()).color(lineColor.r, lineColor.g, lineColor.b, lineColor.a);
        builder.vertex((float) pos2.getX(), (float) pos2.getY(), (float) pos2.getZ()).color(lineColor.r, lineColor.g, lineColor.b, lineColor.a);
        RenderUtil.end(builder);

        RenderSystem.enableDepthTest();
    }


    private static void end(BufferBuilder builder) {
        try (BuiltBuffer meshData = builder.end()) {
            BufferRenderer.drawWithGlobalProgram(meshData);
        } catch (Exception ignore) {
        }
    }
}
