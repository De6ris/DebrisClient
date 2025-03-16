package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.feat.AutoRepeat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatListener {
    public static void onMessageAdd(MinecraftClient client, Text message) {
        AutoRepeat.handleAutoRepeat(client, message);
    }
}
