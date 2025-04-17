package com.github.Debris.DebrisClient.unsafe.miniHud;

import com.github.Debris.DebrisClient.feat.BlockInteractor;
import com.github.Debris.DebrisClient.feat.EntityInteractor;
import fi.dy.masa.minihud.config.Configs;

import java.util.List;

public class MiniHudAccessor {
    public static boolean isPreviewingInventory() {
        return Configs.Generic.INVENTORY_PREVIEW_ENABLED.getBooleanValue() && Configs.Generic.INVENTORY_PREVIEW.getKeybind().isKeybindHeld();
    }

    public static void onLinesUpdate(List<String> lines) {
        if (BlockInteractor.running()) {
            lines.add("方块交互: 还剩" + BlockInteractor.size() + "处");
        }
        if (EntityInteractor.running()) {
            lines.add("实体交互: 还剩" + EntityInteractor.size() + "处");
        }
    }
}
