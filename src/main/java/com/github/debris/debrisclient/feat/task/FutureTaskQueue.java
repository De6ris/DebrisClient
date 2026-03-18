package com.github.debris.debrisclient.feat.task;

import com.github.debris.debrisclient.util.Predicates;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class FutureTaskQueue {
    private static final int DEFAULT_TIMEOUT = 5;

    private static int TICK_COUNTER = 0;

    private static final List<RetryTaskEntry> RETRY_TASKS = new ArrayList<>();

    private static final List<ScheduledTask> NEXT_TICK_TASKS = new ArrayList<>();

    private static final List<ScheduledTaskEntry> SCHEDULED_TASKS = new ArrayList<>();

    @SuppressWarnings("RedundantIfStatement")
    public static void onClientTick(Minecraft client) {
        if (!checkInGame(client)) return;
        RETRY_TASKS.removeIf(entry -> {
            if (entry.task().execute(client)) return true;
            if (entry.deleteTick() == TICK_COUNTER) return true;
            return false;
        });
        NEXT_TICK_TASKS.forEach(x -> x.execute(client));
        NEXT_TICK_TASKS.clear();
        SCHEDULED_TASKS.removeIf(x -> {
            if (x.deleteTick == TICK_COUNTER) {
                x.task.execute(client);
                return true;
            }
            return false;
        });
        TICK_COUNTER++;
    }

    private static boolean checkInGame(Minecraft client) {
        if (Predicates.notInGame(client)) {
            RETRY_TASKS.clear();
            NEXT_TICK_TASKS.clear();
            SCHEDULED_TASKS.clear();
            return false;
        }
        return true;
    }

    public static void add(RetryTask task) {
        add(task, DEFAULT_TIMEOUT);
    }

    public static void add(RetryTask task, int timeout) {
        RETRY_TASKS.add(new RetryTaskEntry(task, TICK_COUNTER + timeout));
    }

    public static void scheduleNextTick(ScheduledTask task) {
        NEXT_TICK_TASKS.add(task);
    }

    public static void schedule(ScheduledTask task, int time) {
        if (time < 0) throw new AssertionError();
        SCHEDULED_TASKS.add(new ScheduledTaskEntry(task, TICK_COUNTER + time));
    }

    private record RetryTaskEntry(RetryTask task, int deleteTick) {
    }

    private record ScheduledTaskEntry(ScheduledTask task, int deleteTick) {
    }
}
