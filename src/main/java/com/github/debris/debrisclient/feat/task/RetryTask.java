package com.github.debris.debrisclient.feat.task;

import net.minecraft.client.Minecraft;

public interface RetryTask {
    /**
     * @return True if task success
     */
    boolean execute(Minecraft client);
}
