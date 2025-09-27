package com.github.debris.debrisclient.localization;

import com.github.debris.debrisclient.DebrisClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface Translatable {
    static TranslationKeyResolver root() {
        return new TranslationKeyResolver(DebrisClient.MOD_ID);
    }

    String getTranslationKey();

    default MutableText text() {
        return Text.translatable(getTranslationKey());
    }

    default MutableText text(Object... args) {
        return Text.translatable(getTranslationKey(), args);
    }
}
