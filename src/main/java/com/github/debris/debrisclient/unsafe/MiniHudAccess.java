package com.github.debris.debrisclient.unsafe;

import com.github.debris.debrisclient.feat.interactor.BlockInteractor;
import com.github.debris.debrisclient.feat.interactor.EntityInteractor;
import fi.dy.masa.minihud.config.Configs;
import fi.dy.masa.minihud.gui.GuiConfigs;

import java.util.List;

public class MiniHudAccess {
    public static boolean isPreviewingInventory() {
        return Configs.Generic.INVENTORY_PREVIEW_ENABLED.getBooleanValue() && Configs.Generic.INVENTORY_PREVIEW.getKeybind().isKeybindHeld();
    }

    public static void onLinesUpdate(List<String> lines) {
        if (BlockInteractor.INSTANCE.hasPending()) {
            lines.add("方块交互: 还剩" + BlockInteractor.INSTANCE.size() + "处");
        }
        if (EntityInteractor.INSTANCE.hasPending()) {
            lines.add("实体交互: 还剩" + EntityInteractor.INSTANCE.size() + "处");
        }
    }

    public static void resetTab() {
        GuiConfigs.tab = GuiConfigs.ConfigGuiTab.GENERIC;
    }

    public static boolean isShapeButton(String content) {
        return content.equals(GuiConfigs.ConfigGuiTab.SHAPES.getDisplayName());
    }
}
