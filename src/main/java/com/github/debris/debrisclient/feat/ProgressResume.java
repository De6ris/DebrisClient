package com.github.debris.debrisclient.feat;

import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public class ProgressResume {
    public static final Map<Class<? extends Screen>, Integer> PROGRESS_MAP = new HashMap<>();

    public static OptionalInt getProgress(Screen guiListBase) {
        Class<? extends Screen> clazz = guiListBase.getClass();
        if (PROGRESS_MAP.containsKey(clazz)) {
            return OptionalInt.of(PROGRESS_MAP.get(clazz));
        }
        return OptionalInt.empty();
    }

    public static void saveProgress(Screen screen, int progress) {
        PROGRESS_MAP.put(screen.getClass(), progress);
    }
}
