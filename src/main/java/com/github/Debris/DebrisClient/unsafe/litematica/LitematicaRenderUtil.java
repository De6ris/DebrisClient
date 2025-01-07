package com.github.Debris.DebrisClient.unsafe.litematica;

import com.mojang.logging.LogUtils;
import fi.dy.masa.litematica.render.OverlayRenderer;
import fi.dy.masa.litematica.selection.Box;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LitematicaRenderUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean setup;
    private static Method renderSelectionBox = null;
    private static Object AREA_SELECTED = null;

    public static void renderSelectionBox(BlockPos pos1, BlockPos pos2, Matrix4f matrix4f) {
        if (!setup) return;
        Box box = new Box(pos1, pos2, "WorldEdit");
        float expand = 0.001f;
        float lineWidthBlockBox = 2f;
        float lineWidthArea = 1.5f;
        try {
            renderSelectionBox.invoke(OverlayRenderer.getInstance(), box, AREA_SELECTED, expand, lineWidthBlockBox, lineWidthArea, null, matrix4f);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.warn("fail to invoke render method", e);
        }
    }

    // I can not widen the BoxType class by aw, do not know why
    private static boolean setupReflection() {
        try {
            Class<OverlayRenderer> clazzOut = OverlayRenderer.class;
            for (Method declaredMethod : clazzOut.getDeclaredMethods()) {
                if (declaredMethod.getName().equals("renderSelectionBox")) {
                    renderSelectionBox = declaredMethod;
                    break;
                }
            }
            Class<?> clazzIn = Class.forName("fi.dy.masa.litematica.render.OverlayRenderer$BoxType");
            Object[] enumConstants = clazzIn.getEnumConstants();
            AREA_SELECTED = enumConstants[0];
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.warn("fail to setup reflection", e);
        }
        return false;
    }

    static {
        setup = setupReflection();
    }
}
