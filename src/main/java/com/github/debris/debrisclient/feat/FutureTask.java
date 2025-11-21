package com.github.debris.debrisclient.feat;

import net.minecraft.client.Minecraft;

public interface FutureTask {
    /**
     * @return Ture if task success
     */
    boolean execute(Minecraft client);

    default int timeout() {
        return 100;
    }
}
