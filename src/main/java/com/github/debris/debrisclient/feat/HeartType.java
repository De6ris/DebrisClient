package com.github.debris.debrisclient.feat;

import net.minecraft.client.gui.hud.InGameHud;

/**
 * A wrapper for vanilla class. Since my config enum uses the class name for translations,
 * vanilla class names will be mapped to bad names, so this is to keep the names.
 * <br>
 * The NONE is an extra option to say no override.
 * <br>
 * Reordered to present the NORMAL first, and other improvements.
 */
public enum HeartType {
    NONE(null),
    NORMAL(InGameHud.HeartType.NORMAL),
    ABSORBING(InGameHud.HeartType.ABSORBING),
    CONTAINER(InGameHud.HeartType.CONTAINER),
    POISONED(InGameHud.HeartType.POISONED),
    WITHERED(InGameHud.HeartType.WITHERED),
    FROZEN(InGameHud.HeartType.FROZEN),
    ;

    private final InGameHud.HeartType type;

    HeartType(InGameHud.HeartType type) {
        this.type = type;
    }

    public InGameHud.HeartType getVanilla() {
        if (this == NONE) throw new IllegalArgumentException();
        return this.type;
    }
}
