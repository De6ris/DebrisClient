package com.github.Debris.DebrisClient.unsafe.mgButtons;

import work.msdnicrosoft.commandbuttons.data.ConfigManager;

public class MGButtonReloader {
    public static void reload() {
        ConfigManager.init();
    }
}
