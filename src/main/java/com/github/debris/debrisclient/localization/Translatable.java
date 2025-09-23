package com.github.debris.debrisclient.localization;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface Translatable {
    String getTranslationKey();

    default MutableText text() {
        return Text.translatable(getTranslationKey());
    }

    default MutableText text(Object... args) {
        return Text.translatable(getTranslationKey(), args);
    }
}
