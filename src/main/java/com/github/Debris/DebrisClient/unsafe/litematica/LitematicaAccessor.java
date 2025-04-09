package com.github.Debris.DebrisClient.unsafe.litematica;

import fi.dy.masa.litematica.config.Hotkeys;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.selection.Box;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class LitematicaAccessor {
    public static boolean isRenderingInfoOverlay() {
        return Hotkeys.RENDER_INFO_OVERLAY.getKeybind().isKeybindHeld();
    }

    public static void selectionRun(Consumer<BlockPos> runnable) {
        AreaSelection areaSelection = DataManager.getSelectionManager().getCurrentSelection();
        if (areaSelection == null) return;
        for (Box box : areaSelection.getAllSubRegionBoxes()) {
            BlockPos pos1 = box.getPos1();
            BlockPos pos2 = box.getPos2();
            if (pos1 == null || pos2 == null) continue;
            BlockPos.stream(pos1, pos2).forEach(runnable);
        }
    }
}
