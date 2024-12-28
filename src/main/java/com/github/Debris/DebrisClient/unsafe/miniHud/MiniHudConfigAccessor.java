package com.github.Debris.DebrisClient.unsafe.miniHud;

import fi.dy.masa.minihud.config.Configs;

public class MiniHudConfigAccessor {
    public static boolean isPreviewingInventory() {
        return Configs.Generic.INVENTORY_PREVIEW_ENABLED.getBooleanValue() && Configs.Generic.INVENTORY_PREVIEW.getKeybind().isKeybindHeld();
    }
}
