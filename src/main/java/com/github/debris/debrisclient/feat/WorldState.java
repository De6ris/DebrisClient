package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.ChatUtil;
import net.minecraft.text.Text;

public class WorldState {
    private static boolean THUNDER = false;

    public static void onThunderSync(boolean thunder) {
        if (!DCCommonConfig.MonitorThunderWeather.getBooleanValue()) return;
        if (THUNDER != thunder) {
            ChatUtil.addLocalMessage(Text.literal(thunder ? "雷暴开始了" : "雷暴结束了"));
        }
        THUNDER = thunder;
    }
}
