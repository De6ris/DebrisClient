package com.github.debris.debrisclient.gui;

import fi.dy.masa.malilib.config.IConfigBase;

import java.util.List;

public record ConfigTab(String name, List<? extends IConfigBase> configs) {

    public String getDisplayName() {
        return this.name;
    }
}
