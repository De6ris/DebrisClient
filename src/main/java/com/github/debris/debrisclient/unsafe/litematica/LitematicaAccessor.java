package com.github.debris.debrisclient.unsafe.litematica;

import com.github.debris.debrisclient.config.DCCommonConfig;
import fi.dy.masa.litematica.config.Hotkeys;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.gui.GuiConfigs;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;

import java.util.stream.Stream;

public class LitematicaAccessor {
    public static boolean isRenderingInfoOverlay() {
        return Hotkeys.RENDER_INFO_OVERLAY.getKeybind().isKeybindHeld();
    }

    public static Stream<BlockPos> streamBlockPos() {
        AreaSelection areaSelection = DataManager.getSelectionManager().getCurrentSelection();
        if (areaSelection == null) return Stream.of();
        return areaSelection.getAllSubRegionBoxes().stream()
                .filter(box -> box.getPos1() != null && box.getPos2() != null)
                .flatMap(box -> BlockPos.stream(box.getPos1(), box.getPos2()));
    }

    public static Stream<BlockBox> streamBlockBox() {
        AreaSelection areaSelection = DataManager.getSelectionManager().getCurrentSelection();
        if (areaSelection == null) return Stream.of();
        return areaSelection.getAllSubRegionBoxes().stream()
                .filter(box -> box.getPos1() != null && box.getPos2() != null)
                .map(box -> BlockBox.create(box.getPos1(), box.getPos2()));
    }

    public static void renderWorldEditSelectionBox(BlockPos pos1, BlockPos pos2, Matrix4f matrix4f) {
        LitematicaRenderUtil.renderSelectionBox(pos1, pos2, matrix4f);// those set up codes from OverlayRenderer.renderBoxes
        // a yellow outline to differ from the original
        RenderUtils.renderAreaSides(pos1, pos2, DCCommonConfig.WorldEditOverlay.getColor(), matrix4f);
    }

    public static void resetTab() {
        DataManager.setConfigGuiTab(GuiConfigs.ConfigGuiTab.GENERIC);
    }

    public static boolean isRenderLayerButton(String content) {
        return content.equals(GuiConfigs.ConfigGuiTab.RENDER_LAYERS.getDisplayName());
    }
}
