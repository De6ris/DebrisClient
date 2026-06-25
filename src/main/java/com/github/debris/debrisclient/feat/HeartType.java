package com.github.debris.debrisclient.feat;

import net.minecraft.client.gui.Hud;

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
    NORMAL(Hud.HeartType.NORMAL),
    ABSORBING(Hud.HeartType.ABSORBING),
    CONTAINER(Hud.HeartType.CONTAINER),
    POISONED(Hud.HeartType.POISIONED),
    WITHERED(Hud.HeartType.WITHERED),
    FROZEN(Hud.HeartType.FROZEN),
    ;

    private final Hud.HeartType type;

    HeartType(Hud.HeartType type) {
        this.type = type;
    }

    public Hud.HeartType getVanilla() {
        if (this == NONE) throw new IllegalArgumentException();
        return this.type;
    }
}
