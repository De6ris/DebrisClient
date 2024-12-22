package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.command.DCAutoRepeatCommand;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Comparator;

public class ChatListener {
    public static void onMessageAdd(MinecraftClient client, Text message) {
        handleAutoRepeat(client, message);
    }

    private static void handleAutoRepeat(MinecraftClient client, Text message) {
        String originalString = message.getString();
        if (DCCommonConfig.AutoRepeatBlackList.getStrings().stream().anyMatch(originalString::contains)) return;
        DCAutoRepeatCommand.streamTrackedPlayers()
                .map(ChatUtil::angleName)
                .flatMap(x -> ChatUtil.filterMessageContent(x, originalString).stream())
                .max(Comparator.comparing(String::length))
                .ifPresent(x -> ChatUtil.sendChat(client, x));
    }
}
