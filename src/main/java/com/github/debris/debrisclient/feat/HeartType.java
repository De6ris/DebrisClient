package com.github.debris.debrisclient.feat;

import net.minecraft.client.gui.Gui;

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
    NORMAL(Gui.HeartType.NORMAL),
    ABSORBING(Gui.HeartType.ABSORBING),
    CONTAINER(Gui.HeartType.CONTAINER),
    POISONED(Gui.HeartType.POISIONED),
    WITHERED(Gui.HeartType.WITHERED),
    FROZEN(Gui.HeartType.FROZEN),
    ;

    private final Gui.HeartType type;

    HeartType(Gui.HeartType type) {
        this.type = type;
    }

    public Gui.HeartType getVanilla() {
        if (this == NONE) throw new IllegalArgumentException();
        return this.type;
    }
}
