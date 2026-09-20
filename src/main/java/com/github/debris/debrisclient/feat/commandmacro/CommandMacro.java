package com.github.debris.debrisclient.feat.commandmacro;

import com.github.debris.debrisclient.feat.CommandQueue;
import com.github.debris.debrisclient.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record CommandMacro(int period, List<String> commands) {

    public static CommandMacro load(JsonObject object) {
        int period = object.get("period").getAsInt();

        JsonArray array = object.get("commands").getAsJsonArray();
        List<String> commands = JsonUtil.readStringArray(array);

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
        return CMStorage.save(file, this);
    }

    @Nullable
    public static Component runFile(String file) {
        CommandMacro macro = CMStorage.get(file);
        if (macro == null) return Component.literal("无此文件");
        macro.run();
        return null;
    }

    public void run() {
        CommandQueue.run(this);
    }

}
