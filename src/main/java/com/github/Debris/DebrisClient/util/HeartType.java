package com.github.Debris.DebrisClient.util;

import net.minecraft.client.gui.hud.InGameHud;

/**
 * A wrapper for vanilla class. Since my config enum uses the class name for translations,
 * vanilla class names will be mapped to bad names, so this is to keep the names.
 */
public enum HeartType {
    CONTAINER(InGameHud.HeartType.CONTAINER),
    NORMAL(InGameHud.HeartType.NORMAL),
    POISONED(InGameHud.HeartType.POISONED),
    WITHERED(InGameHud.HeartType.WITHERED),
    ABSORBING(InGameHud.HeartType.ABSORBING),
    FROZEN(InGameHud.HeartType.FROZEN),
    ;

    private final InGameHud.HeartType type;

    HeartType(InGameHud.HeartType type) {
        this.type = type;
    }

    public InGameHud.HeartType getVanilla() {
        return this.type;
    }
}
