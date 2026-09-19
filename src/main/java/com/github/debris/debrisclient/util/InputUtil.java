package com.github.debris.debrisclient.util;

import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.util.input.KeyCodes;

public class InputUtil {
    public static boolean isShiftDown() {
        return KeybindMulti.isKeyDown(KeyCodes.KEY_LEFT_SHIFT);
    }

    public static boolean isCtrlDown() {
        return KeybindMulti.isKeyDown(KeyCodes.KEY_LEFT_CONTROL);
    }

    public static boolean isLeftClicking() {
        return KeybindMulti.isKeyDown(KeyCodes.MOUSE_LEFT);
    }
}
