package com.github.debris.debrisclient.unsafe;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.unsafe.litematica.LitematicaRenderUtil;
import com.github.debris.debrisclient.util.AccessorUtil;
import com.github.debris.debrisclient.util.ScreenUtil;
import fi.dy.masa.litematica.config.Hotkeys;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.gui.GuiConfigs;
import fi.dy.masa.litematica.gui.GuiMaterialList;
import fi.dy.masa.litematica.gui.widgets.WidgetListMaterialList;
import fi.dy.masa.litematica.gui.widgets.WidgetMaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.joml.Matrix4f;

import java.util.Optional;
import java.util.stream.Stream;

public class LitematicaAccess {
    public static boolean isRenderingInfoOverlay() {
        return Hotkeys.RENDER_INFO_OVERLAY.getKeybind().isKeybindHeld();
    }

    public static Stream<BlockPos> streamBlockPos() {
        AreaSelection areaSelection = DataManager.getSelectionManager().getCurrentSelection();
        if (areaSelection == null) return Stream.of();
        return areaSelection.getAllSubRegionBoxes().stream()
                .filter(box -> box.getPos1() != null && box.getPos2() != null)
                .flatMap(box -> BlockPos.betweenClosedStream(box.getPos1(), box.getPos2()));
    }

    public static Stream<BoundingBox> streamBlockBox() {
        AreaSelection areaSelection = DataManager.getSelectionManager().getCurrentSelection();
        if (areaSelection == null) return Stream.of();
        return areaSelection.getAllSubRegionBoxes().stream()
                .filter(box -> box.getPos1() != null && box.getPos2() != null)
                .map(box -> BoundingBox.fromCorners(box.getPos1(), box.getPos2()));
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

    public static boolean isMaterialListScreen(Screen screen) {
        return screen instanceof GuiMaterialList;
    }

    public static ItemStack getHoveredStack(Screen screen) {
        if (screen instanceof GuiMaterialList guiMaterialList) {
            WidgetListMaterialList listWidget = AccessorUtil.getListWidget(guiMaterialList);
            Optional<WidgetMaterialListEntry> optional = ScreenUtil.getHoveredWidget(listWidget);
            if (optional.isPresent()) {
                MaterialListEntry materialEntry = optional.get().getEntry();
                if (materialEntry != null) return materialEntry.getStack();
            }
        }
        return ItemStack.EMPTY;
    }
}
