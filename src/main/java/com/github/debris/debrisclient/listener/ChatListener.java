package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.feat.AutoRepeat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatListener {
    public static void onMessageAdd(MinecraftClient client, Text message) {
        AutoRepeat.handleAutoRepeat(client, message);
    }
}
