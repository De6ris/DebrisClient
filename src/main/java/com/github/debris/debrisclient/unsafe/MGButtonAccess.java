package com.github.debris.debrisclient.unsafe;

import work.msdnicrosoft.commandbuttons.data.ConfigManager;

public class MGButtonAccess {
    public static void reload() {
        ConfigManager.init();
    }
}
