package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.Predicates;
import net.minecraft.client.Minecraft;

public class AutoRotate {
    @SuppressWarnings("DataFlowIssue")
    public static void onClientTick(Minecraft client) {
        if (DCCommonConfig.AUTO_ROTATE.getBooleanValue() && Predicates.inGameNoGui(client)) {
            client.player.turn(120, 0);
        }
    }
}
