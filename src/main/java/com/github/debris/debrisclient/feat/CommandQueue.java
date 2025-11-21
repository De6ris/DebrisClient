package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.util.ChatUtil;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class CommandQueue {
    private static int TICK_COUNTER = 0;

    private static final List<CommandEntry> ENTRIES = new ArrayList<>();

    public static void add(String command, int runAfter) {
        ENTRIES.add(new CommandEntry(command, TICK_COUNTER + runAfter));
    }

    public static void onClientTick(Minecraft client) {
        ENTRIES.removeIf(entry -> {
            if (entry.runTick == TICK_COUNTER) {
                ChatUtil.sendChat(client, entry.command);
                return true;
            }
            return false;
        });
        TICK_COUNTER++;
    }

    public static void stop() {
        ENTRIES.clear();
    }

    public static void run(CommandMacro macro) {
        int period = macro.period();
        List<String> commands = macro.commands();
        for (int i = 0; i < commands.size(); i++) {
            add(commands.get(i), period * i);
        }
    }

    private record CommandEntry(String command, int runTick) {
    }
}
