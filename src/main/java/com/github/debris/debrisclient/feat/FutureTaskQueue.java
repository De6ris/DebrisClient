package com.github.debris.debrisclient.feat;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class FutureTaskQueue {
    private static int TICK_COUNTER = 0;

    private static final List<TaskEntry> ENTRIES = new ArrayList<>();

    private static final List<Runnable> NEXT_TICK_TASKS = new ArrayList<>();

    public static void onClientTick(Minecraft client) {
        NEXT_TICK_TASKS.forEach(Runnable::run);
        NEXT_TICK_TASKS.clear();
        ENTRIES.removeIf(entry -> {
            if (entry.task().execute(client)) return true;
            if (entry.deleteTick() == TICK_COUNTER) return true;
            return false;
        });
        TICK_COUNTER++;
    }

    public static void addTask(FutureTask task) {
        ENTRIES.add(new TaskEntry(task, TICK_COUNTER + task.timeout()));
    }

    public static void addNextTick(Runnable task) {
        NEXT_TICK_TASKS.add(task);
    }

    private record TaskEntry(FutureTask task, int deleteTick) {
    }
}
