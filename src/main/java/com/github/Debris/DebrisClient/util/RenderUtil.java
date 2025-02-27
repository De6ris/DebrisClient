package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.unsafe.litematica.LitematicaRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RenderUtil {
    @SuppressWarnings("ConstantConditions")
    public static void drawConnectLine(Vec3d pos1, Vec3d pos2, double boxLength, Color4f pos1Color, Color4f pos2Color, @NotNull Color4f lineColor) {
        RenderSystem.disableDepthTest();

        Vec3d camPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        // do not use EntityUtils.getCameraEntity().getPos(), that is not real camera position

        pos1 = pos1.subtract(camPos);
        pos2 = pos2.subtract(camPos);

        Tessellator tesselator = Tessellator.getInstance();

        fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);
        fi.dy.masa.malilib.render.RenderUtils.setupBlend();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
//        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
//        RenderSystem.applyModelViewMatrix();
        // what they do? can not render if commented
        // can render now, but theory still unknown

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

    public static void renderWorldEditSelectionBox(BlockPos pos1, BlockPos pos2, Matrix4f matrix4f, MinecraftClient client) {
        if (FabricLoader.getInstance().isModLoaded(ModReference.Litematica)) {
            fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);
            fi.dy.masa.malilib.render.RenderUtils.setupBlend();

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);

            RenderSystem.enablePolygonOffset();
            RenderSystem.polygonOffset(-1.2f, -0.2f);

            LitematicaRenderUtil.renderSelectionBox(pos1, pos2, matrix4f);// those set up codes from OverlayRenderer.renderBoxes
            // a yellow outline to differ from the original
            fi.dy.masa.litematica.render.RenderUtils.renderAreaSides(pos1, pos2, DCCommonConfig.WorldEditOverlay.getColor(), matrix4f, client);

            RenderSystem.polygonOffset(0f, 0f);
            RenderSystem.disablePolygonOffset();

            RenderSystem.depthMask(true);
        }
    }


    private static void end(BufferBuilder builder) {
        try (BuiltBuffer meshData = builder.end()) {
            BufferRenderer.drawWithGlobalProgram(meshData);
        } catch (Exception ignore) {
        }
    }
}
