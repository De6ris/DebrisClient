package com.github.debris.debrisclient.feat;

import net.minecraft.client.MinecraftClient;

public interface FutureTask {
    /**
     * @return Ture if task success
     */
    boolean execute(MinecraftClient client);

    default int timeout() {
        return 100;
    }
}
