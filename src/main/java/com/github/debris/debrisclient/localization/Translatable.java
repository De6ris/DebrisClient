package com.github.debris.debrisclient.localization;

import com.github.debris.debrisclient.DebrisClient;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface Translatable {
    static TranslationKeyResolver root() {
        return new TranslationKeyResolver(DebrisClient.MOD_ID);
    }

    String getTranslationKey();

    default MutableComponent translate() {
        return Component.translatable(getTranslationKey());
    }

    default MutableComponent translate(Object... args) {
        return Component.translatable(getTranslationKey(), args);
    }
}
