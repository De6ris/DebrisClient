package com.github.Debris.DebrisClient.config.options;

import com.github.Debris.DebrisClient.config.api.IConfigEnum;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigOptionList;

public class ConfigOptionListExtend extends ConfigOptionList implements IConfigEnum {
    public ConfigOptionListExtend(String name, IConfigOptionListEntry defaultValue, String comment) {
        super(name, defaultValue, comment);
    }

    public ConfigOptionListExtend(String name, IConfigOptionListEntry defaultValue, String comment, String prettyName) {
        super(name, defaultValue, comment, prettyName);
    }
}
