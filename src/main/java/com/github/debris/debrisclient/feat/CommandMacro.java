package com.github.debris.debrisclient.feat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;

public record CommandMacro(int period, List<String> commands) {
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
}
