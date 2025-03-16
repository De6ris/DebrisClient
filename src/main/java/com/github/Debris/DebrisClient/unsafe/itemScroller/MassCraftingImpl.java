package com.github.Debris.DebrisClient.unsafe.itemScroller;

import java.util.function.Supplier;

public enum MassCraftingImpl {
    RECIPE_BOOK(MassCraftingRecipeBook::new),
    MANUAL(MassCraftingManual::new),
    ;

    private final Supplier<AbstractMassCrafting> factory;

    MassCraftingImpl(Supplier<AbstractMassCrafting> factory) {
        this.factory = factory;
    }

    public AbstractMassCrafting create() {
        return factory.get();
    }
}
