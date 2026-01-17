package com.github.debris.debrisclient.unsafe;

import com.github.debris.debrisclient.unsafe.libgui.CMScreen;
import net.minecraft.client.gui.screens.Screen;

public class CMGuiAccess {
    public static Screen getScreen() {
        return CMScreen.INSTANCE;
    }
}
