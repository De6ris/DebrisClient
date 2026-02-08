package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ItemBotMapping {
    private static final Path PATH = DebrisClient.CONFIG_DIR.resolve("item_bot_mapping.json");

    private static boolean initialized;

    private static final Map<Identifier, List<String>> ID_MAP = new HashMap<>();
    private static final Map<Identifier, List<String>> TAG_MAP = new HashMap<>();

    private static void initialize() {
        if (!Files.exists(PATH)) {
            JsonUtils.writeJsonToFileAsPath(new JsonObject(), PATH);
            return;
        }
        reload();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Nullable
    public static Component reload() {
        ID_MAP.clear();
        TAG_MAP.clear();

        Either<JsonObject, Component> either = JsonUtil.readJsonFromFile(PATH);
        if (either.right().isPresent()) return either.right().get();

        JsonObject object = either.left().get();
        object.asMap().forEach((key, element) -> {
            if (!element.isJsonArray()) return;
            JsonArray jsonArray = element.getAsJsonArray();
            List<String> strings = JsonUtil.readStringArray(jsonArray);
            if (strings.isEmpty()) return;
            if (key.startsWith("#")) {
                TAG_MAP.put(Identifier.parse(key.substring(1)), strings);
            } else {
                ID_MAP.put(Identifier.parse(key), strings);
            }
        });
        return null;
    }

    public static List<String> getNames(ItemStack stack) {
        if (!initialized) {
            initialize();
            initialized = true;
        }

        String prefix = DCCommonConfig.SpawnBotPrefix.getStringValue();

        Stream<String> tagStream = stack.getTags()
                .map(TagKey::location)
                .filter(TAG_MAP::containsKey)
                .map(TAG_MAP::get)
                .flatMap(Collection::stream);

        Identifier identifier = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Stream<String> idStream = ID_MAP.containsKey(identifier) ? ID_MAP.get(identifier).stream() : Stream.of();

        List<String> aliases = Stream.concat(idStream, tagStream)
                .distinct()
                .map(x -> {
                    if (x.startsWith(prefix)) return x;
                    return prefix + x;
                })
                .toList();

        return aliases.isEmpty() ? List.of(prefix + identifier.getPath()) : aliases;
    }
}
