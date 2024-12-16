package com.github.Debris.DebrisClient.config.options;

import com.github.Debris.DebrisClient.config.api.IConfigTrigger;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

public class ConfigHotKeyExtend extends ConfigHotkey implements IConfigTrigger {
    public ConfigHotKeyExtend(String name, String defaultStorageString, String comment) {
        super(name, defaultStorageString, comment);
    }

    public ConfigHotKeyExtend(String name, String defaultStorageString, KeybindSettings settings, String comment) {
        super(name, defaultStorageString, settings, comment);
    }

    public ConfigHotKeyExtend(String name, String defaultStorageString, String comment, String prettyName) {
        super(name, defaultStorageString, comment, prettyName);
    }

    public ConfigHotKeyExtend(String name, String defaultStorageString, KeybindSettings settings, String comment, String prettyName) {
        super(name, defaultStorageString, settings, comment, prettyName);
    }
}
