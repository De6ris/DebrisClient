package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.ChatUtil;
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
