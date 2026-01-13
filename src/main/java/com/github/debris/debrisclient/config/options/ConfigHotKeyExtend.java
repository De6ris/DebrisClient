package com.github.debris.debrisclient.config.options;

import com.github.debris.debrisclient.config.api.IConfigTrigger;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

public class ConfigHotKeyExtend extends ConfigHotkey implements IConfigTrigger {
    public ConfigHotKeyExtend(String name, String defaultStorageString, KeybindSettings settings, String comment) {
        super(name, defaultStorageString, settings, comment);
    }
}
