package com.github.debris.debrisclient.feat.log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractGameLog implements GameLog {
    private final Map<String, String> options = makeOptions();

    private boolean active = false;

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void readJson(JsonElement jsonElement) {
        if (!jsonElement.isJsonObject()) return;
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("active")) {
            this.active = jsonObject.get("active").getAsBoolean();
        }
        for (String s : this.options.keySet()) {
            if (jsonObject.has(s)) {
                this.options.replace(s, jsonObject.get(s).getAsString());
            }
        }
    }

    @Override
    public JsonElement writeJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("active", new JsonPrimitive(this.active));
        this.options.forEach((x, y) -> jsonObject.add(x, new JsonPrimitive(y)));
        return jsonObject;
    }

    @Override
    public Collection<String> getOptionNames() {
        return this.options.keySet();
    }

    @Override
    public void setOption(String option, String value) {
        this.options.replace(option, value);
    }

    @Override
    public boolean hasOption(String option) {
        return this.options.containsKey(option);
    }

    @Override
    public String getOption(String option) {
        return this.options.get(option);
    }

    private Map<String, String> makeOptions() {
        HashMap<String, String> map = new HashMap<>();
        this.registerOptions(map);
        return map;
    }

    protected void registerOptions(Map<String, String> map) {
    }
}
