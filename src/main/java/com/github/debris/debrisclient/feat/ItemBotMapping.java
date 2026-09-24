package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.JsonUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import fi.dy.masa.malilib.util.data.json.JsonUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class ItemBotMapping {
    public static final String ID_SPLIT = ":";
    public static final String TAG_PREFIX = "#";
    public static final String TAG_SPLIT = "/";

    private static final Path PATH = DebrisClient.CONFIG_DIR.resolve("item_bot_mapping.json");

    private static final Multimap<Identifier, String> ID_MAP = HashMultimap.create();
    private static final Multimap<Identifier, String> TAG_MAP = HashMultimap.create();

    private static void initialize() {
        if (!Files.exists(PATH)) {
            JsonUtils.writeJsonToFile(new JsonObject(), PATH);
            return;
        }
        load();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Nullable
    public static Component load() {
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
            if (key.startsWith(TAG_PREFIX)) {
                TAG_MAP.putAll(Identifier.parse(key.substring(1)), strings);
            } else {
                ID_MAP.putAll(Identifier.parse(key), strings);
            }
        });
        return null;
    }

    public static void save() {
        JsonObject jsonObject = new JsonObject();

        for (Identifier identifier : TAG_MAP.keySet()) {
            JsonArray jsonArray = new JsonArray();
            TAG_MAP.get(identifier).forEach(jsonArray::add);
            jsonObject.add(TAG_PREFIX + identifier.toString(), jsonArray);
        }

        for (Identifier identifier : ID_MAP.keySet()) {
            JsonArray jsonArray = new JsonArray();
            ID_MAP.get(identifier).forEach(jsonArray::add);
            jsonObject.add(identifier.toString(), jsonArray);
        }

        JsonUtils.writeJsonToFile(jsonObject, PATH);
    }

    public static void add(String key, String name) {
        if (key.startsWith(TAG_PREFIX)) {
            TAG_MAP.put(Identifier.parse(key.substring(1)), name);
        } else {
            ID_MAP.put(Identifier.parse(key), name);
        }

        save();
    }

    public static List<String> getNames(ItemStack stack) {
        Stream<String> tagStream = stack.tags()
                .map(TagKey::location)
                .filter(TAG_MAP::containsKey)
                .map(TAG_MAP::get)
                .flatMap(Collection::stream);

        Identifier identifier = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Stream<String> idStream = ID_MAP.containsKey(identifier) ? ID_MAP.get(identifier).stream() : Stream.of();

        List<String> aliases = Stream.concat(idStream, tagStream).toList();

        String prefix = DCCommonConfig.SpawnBotPrefix.getStringValue();

        List<String> ret = aliases.isEmpty() ? List.of(identifier.getPath()) : aliases;
        return ret.stream().distinct()
                .map(x -> {
                    if (x.startsWith(prefix)) return x;
                    return prefix + x;
                })
                .toList();
    }

    public static List<String> suggestKey(ItemStack stack) {
        if (stack.isEmpty()) return List.of();

        List<String> list = new ArrayList<>();

        list.add(
                quoted(
                        BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath()
                )
        );
        list.addAll(
                stack.tags()
                        .map(TagKey::location)
                        .map(x -> quoted(TAG_PREFIX + x))
                        .toList()
        );

        return list;
    }

    public static Stream<String> suggestName(String string) {
        if (string.startsWith(TAG_PREFIX)) {
            string = string.substring(1);
            if (isPlural(string)) {
                String[] split = string.split("[:_/]", -1);
                return Stream.concat(Arrays.stream(split), Stream.of(getSingular(split[split.length - 1])));
            }
        }
        return Arrays.stream(string.split("[:_/]", -1));
    }

    private static boolean isPlural(String tag) {
        return tag.endsWith("s");
    }

    private static String getSingular(String tag) {
        return tag.substring(0, tag.length() - 1);
    }

    private static String quoted(String s) {
        return "\"" + s + "\"";
    }

    static {
        initialize();
    }
}
