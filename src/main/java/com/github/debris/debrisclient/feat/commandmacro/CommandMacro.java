package com.github.debris.debrisclient.feat.commandmacro;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.feat.CommandQueue;
import com.github.debris.debrisclient.localization.CommandMacroText;
import com.google.gson.*;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record CommandMacro(int period, List<String> commands) {
    public static final Path MACRO_DIR = DebrisClient.CONFIG_DIR.resolve("command_macros");

    public static CommandMacro load(JsonObject object) {
        int period = object.get("period").getAsInt();

        JsonArray array = object.get("commands").getAsJsonArray();
        List<String> commands = array.asList().stream().map(JsonElement::getAsString).toList();

        return new CommandMacro(period, commands);
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();

        object.add("period", new JsonPrimitive(this.period));

        JsonArray array = new JsonArray();
        for (String command : commands) {
            array.add(command);
        }
        object.add("commands", array);

        return object;
    }

    public boolean saveToFile(String file) {
        File folder = MACRO_DIR.toFile();
        if ((folder.exists() && folder.isDirectory()) || folder.mkdirs()) {
            return JsonUtils.writeJsonToFileAsPath(this.save(), MACRO_DIR.resolve(file));
        }
        return false;
    }

    @Nullable
    public static Component runFile(String file) {
        Path filePath = CommandMacro.MACRO_DIR.resolve(file);

        if (!Files.exists(filePath)) {
            return CommandMacroText.FILE_NOT_FOUND.translate();
        }

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (!jsonElement.isJsonObject()) {
                return CommandMacroText.NOT_JSON.translate();
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            CommandMacro macro = CommandMacro.load(jsonObject);
            if (macro.period() < 0) {
                return CommandMacroText.ILLEGAL_PERIOD.translate();
            }
            macro.run();
        } catch (Exception e) {
            return CommandMacroText.READ_FILE_ERROR.translate();
        }

        return null;
    }

    public void run() {
        CommandQueue.run(this);
    }

}
