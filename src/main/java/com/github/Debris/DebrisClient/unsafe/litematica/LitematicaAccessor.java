package com.github.Debris.DebrisClient.unsafe.litematica;

import fi.dy.masa.litematica.config.Hotkeys;

public class LitematicaAccessor {
    public static boolean isRenderingInfoOverlay() {
        return Hotkeys.RENDER_INFO_OVERLAY.getKeybind().isKeybindHeld();
    }
}
