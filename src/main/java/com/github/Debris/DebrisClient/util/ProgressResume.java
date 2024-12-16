package com.github.Debris.DebrisClient.util;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiListBase;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public class ProgressResume {
    public static final Map<Class<? extends GuiConfigsBase>, Integer> PROGRESS = new HashMap<>();

    public static OptionalInt getProgress(GuiListBase guiListBase) {
        Class<? extends GuiListBase> clazz = guiListBase.getClass();
        if (PROGRESS.containsKey(clazz)) {
            return OptionalInt.of(PROGRESS.get(clazz));
        }
        return OptionalInt.empty();
    }

    public static void saveProgress(GuiConfigsBase guiListBase, int progress) {
        PROGRESS.put(guiListBase.getClass(), progress);
    }
}
