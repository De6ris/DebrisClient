package com.github.debris.debrisclient.feat.log;

import com.github.debris.debrisclient.DebrisClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;
import org.jspecify.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class GameLogs {
    private static final Map<String, GameLog> MAP = new HashMap<>();


    public static final PortalLog PORTAL = register("portal", new PortalLog());
    public static final ThunderLog THUNDER = register("thunder", new ThunderLog());
    public static final PathNodeLog PATH_NODE = register("path_node", new PathNodeLog());


    private static final Path CONFIG_FILE = DebrisClient.CONFIG_DIR.resolve("game_log.json");

    private static <T extends GameLog> T register(String thunder, T log) {
        MAP.put(thunder, log);
        return log;
    }

    public static @Nullable GameLog getLog(String category) {
        return MAP.get(category);
    }

    public static Collection<GameLog> getLogs() {
        return MAP.values();
    }

    public static Set<String> getCategories() {
        return MAP.keySet();
    }

    public static void loadOrCreate() {
        if (!Files.exists(CONFIG_FILE)) {
            save();
        } else {
            load();
        }
    }

    private static void load() {
        JsonElement jsonElement = JsonUtils.parseJsonFileAsPath(CONFIG_FILE);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            MAP.forEach((s, log) -> {
                if (jsonObject.has(s)) {
                    log.readJson(jsonObject.get(s));
                }
            });
        }
    }

    public static void save() {
        JsonObject jsonObject = new JsonObject();
        MAP.forEach((s, log) -> jsonObject.add(s, log.writeJson()));
        JsonUtils.writeJsonToFileAsPath(jsonObject, CONFIG_FILE);
    }
}
