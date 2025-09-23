package com.github.debris.debrisclient.datagen;

import com.github.debris.debrisclient.config.options.ConfigEnum;
import com.github.debris.debrisclient.config.options.ConfigEnumEntryWrapper;
import com.github.debris.debrisclient.localization.Translatable;

import java.util.Arrays;
import java.util.List;

public class LocalizationGen {
    public static <T extends Translatable> List<String> generate(Class<T> clazz) {
        return Arrays.stream(clazz.getEnumConstants()).map(Translatable::getTranslationKey).toList();
    }

    public static List<String> generate(ConfigEnum<?> configEnum) {
        return configEnum.getWrappers().stream()
                .map(ConfigEnumEntryWrapper::getTranslationKey)
                .toList();
    }

    public static void printAsJsonElements(List<String> keys) {
        keys.forEach(key -> System.out.printf("\"%s\": \"%s\",%n", key, ""));
    }
}
