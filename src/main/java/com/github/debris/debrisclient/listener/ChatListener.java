package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.feat.AutoRepeat;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ChatListener {
    public static void onMessageAdd(Minecraft client, Component message) {
        AutoRepeat.handleAutoRepeat(client, message);
    }
}
