package com.github.Debris.DebrisClient.unsafe.itemScroller;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

import java.util.function.Supplier;

public enum MassCraftingImpl implements IConfigOptionListEntry {
    RECIPE_BOOK("recipe_book", "配方书", MassCraftingRecipeBook::new),
    MANUAL("manual", "手动", MassCraftingManual::new),
    ;

    private final String configString;
    private final String translationKey;
    private final Supplier<AbstractMassCrafting> factory;

    MassCraftingImpl(String configString, String translationKey, Supplier<AbstractMassCrafting> factory) {
        this.configString = configString;
        this.translationKey = translationKey;
        this.factory = factory;
    }

    @Override
    public String getStringValue() {
        return this.configString;
    }

    @Override
    public String getDisplayName() {
        return this.translationKey;
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();
        if (forward) {
            if (++id >= values().length) {
                id = 0;
            }
        } else {
            if (--id < 0) {
                id = values().length - 1;
            }
        }
        return values()[id % values().length];
    }

    @Override
    public IConfigOptionListEntry fromString(String name) {
        return fromStringStatic(name);
    }

    public static MassCraftingImpl fromStringStatic(String name) {
        for (MassCraftingImpl val : values()) {
            if (val.configString.equalsIgnoreCase(name)) {
                return val;
            }
        }
        return MassCraftingImpl.RECIPE_BOOK;
    }

    public AbstractMassCrafting create() {
        return factory.get();
    }
}
