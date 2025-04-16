package com.github.Debris.DebrisClient.unsafe.litematica;

import fi.dy.masa.litematica.config.Hotkeys;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.selection.AreaSelection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

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
}
