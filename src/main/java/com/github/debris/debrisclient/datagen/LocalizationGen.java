package com.github.debris.debrisclient.datagen;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.config.options.ConfigEnum;
import com.github.debris.debrisclient.config.options.ConfigEnumEntryWrapper;
import com.github.debris.debrisclient.localization.Translatable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.util.JsonUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class LocalizationGen {
    private static final List<String> LANG_FILES = List.of("en_us", "zh_cn");

    public static List<String> generate(Class<? extends Translatable> clazz) {
        return Arrays.stream(clazz.getEnumConstants())
                .map(Translatable::getTranslationKey)
                .toList();
    }

    public static List<String> generate(ConfigEnum<?> configEnum) {
        return configEnum.getWrappers().stream()
                .map(ConfigEnumEntryWrapper::getTranslationKey)
                .toList();
    }

    public static void addMissingKeys(List<String> keys) {
        for (String langFile : LANG_FILES) {
            String json = langFile + ".json";
            Path path = Path.of("src/main/resources/assets/")
                    .resolve(DebrisClient.MOD_ID)
                    .resolve("lang")
                    .resolve(json);
            addMissingKeys(path, keys);
        }
    }

    public static void addMissingKeys(Path path, List<String> keys) {
        JsonObject base;
        if (!Files.exists(path)) {
            base = new JsonObject();
        } else {
            JsonElement jsonElement = JsonUtils.parseJsonFileAsPath(path);
            if (jsonElement == null) throw new AssertionError();
            base = jsonElement.getAsJsonObject();
        }
        for (String key : keys) {
            if (base.has(key)) continue;
            base.add(key, new JsonPrimitive(""));
        }
        JsonUtils.writeJsonToFileAsPath(base, path);
    }
}
