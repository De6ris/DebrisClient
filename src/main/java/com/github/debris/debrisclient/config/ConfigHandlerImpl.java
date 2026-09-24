package com.github.debris.debrisclient.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static com.github.debris.debrisclient.DebrisClient.MOD_NAME;

public class ConfigHandlerImpl implements IConfigHandler {
    private final Path path;
    private final List<? extends IConfigBase> configs;

    public ConfigHandlerImpl(Path path, List<? extends IConfigBase> configs) {
        this.path = path;
        this.configs = configs;
    }

    @Override
    public void load() {
        File settingFile = this.path.toFile();
        if (settingFile.isFile() && settingFile.exists()) {
            JsonElement jsonElement = JsonUtils.parseJsonFile(this.path);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject obj = jsonElement.getAsJsonObject();
                ConfigUtils.readConfigBase(obj, MOD_NAME, this.configs);
            }
        }
    }

    @Override
    public void save() {
        File folder = this.path.getParent().toFile();
        if ((folder.exists() && folder.isDirectory()) || folder.mkdirs()) {
            JsonObject configRoot = new JsonObject();
            ConfigUtils.writeConfigBase(configRoot, MOD_NAME, this.configs);
            JsonUtils.writeJsonToFile(configRoot, this.path);
        }
    }
}
