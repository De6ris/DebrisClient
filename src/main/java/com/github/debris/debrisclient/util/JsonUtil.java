package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.localization.GeneralText;
import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonUtil {
    public static Either<JsonObject, Component> readJsonFromFile(Path path) {
        if (!Files.exists(path)) {
            return Either.right(GeneralText.FILE_NOT_FOUND.translate());
        }
        try (InputStream inputStream = Files.newInputStream(path)) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (!jsonElement.isJsonObject()) {
                return Either.right(GeneralText.NOT_JSON.translate());
            }
            return Either.left(jsonElement.getAsJsonObject());
        } catch (Exception e) {
            return Either.right(GeneralText.READ_FILE_ERROR.translate());
        }
    }

    public static List<String> readStringArray(JsonArray jsonArray) {
        return jsonArray.asList().stream()
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsJsonPrimitive)
                .filter(JsonPrimitive::isString)
                .map(JsonPrimitive::getAsString)
                .toList();
    }
}
