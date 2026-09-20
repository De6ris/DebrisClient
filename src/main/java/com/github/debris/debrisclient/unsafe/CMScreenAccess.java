package com.github.debris.debrisclient.unsafe;

import com.github.debris.debrisclient.unsafe.libgui.CMTitleScreen;
import net.minecraft.client.gui.screens.Screen;

public class CMScreenAccess {
    public static Screen getCMScreen() {
        return CMTitleScreen.INSTANCE;
    }
}
