package com.github.debris.debrisclient.feat.commandmacro;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.util.JsonUtil;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.data.json.JsonUtils;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class CMStorage {
    public static final Path MACRO_DIR = DebrisClient.CONFIG_DIR.resolve("command_macros");
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<String, CommandMacro> MAP = new TreeMap<>();

    public static void markDirty() {

    }

    @Nullable
    public static Component reload() {
        MAP.clear();
        try (Stream<Path> stream = Files.list(MACRO_DIR)) {
            stream.filter(Files::isRegularFile)
                    .map(x -> x.getFileName().toString())
                    .forEach(x -> JsonUtil.readJsonFromFile(MACRO_DIR.resolve(x)).ifLeft(
                            jsonObject -> MAP.put(x, CommandMacro.load(jsonObject))
                    ));
        } catch (IOException e) {
            return Component.literal(e.getMessage());
        }
        return null;
    }

    public static boolean save(String file, CommandMacro macro) {
        File folder = CMStorage.MACRO_DIR.toFile();
        if ((folder.exists() && folder.isDirectory()) || folder.mkdirs()) {
            boolean success = JsonUtils.writeJsonToFile(macro.save(), CMStorage.MACRO_DIR.resolve(file));
            if (success) MAP.put(file, macro);
            return success;
        }
        return false;
    }

    public static Stream<String> streamFiles() {
        return MAP.keySet().stream();
    }

    @Nullable
    public static CommandMacro get(String file) {
        return MAP.get(file);
    }

    static {
        reload();
    }
}
