package com.github.debris.debrisclient.util;

import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.util.KeyCodes;

public class InputUtil {
    public static boolean isLeftClicking() {
        return KeybindMulti.isKeyDown(KeyCodes.MOUSE_BUTTON_1);
    }
}
