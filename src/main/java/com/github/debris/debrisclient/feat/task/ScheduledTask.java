package com.github.debris.debrisclient.feat.task;

import net.minecraft.client.Minecraft;

public interface ScheduledTask {
    void execute(Minecraft client);
}
