package com.github.debris.debrisclient.unsafe.mgButtons;

import work.msdnicrosoft.commandbuttons.data.ConfigManager;

public class MGButtonReloader {
    public static void reload() {
        ConfigManager.init();
    }
}
