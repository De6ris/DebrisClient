package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.command.DCAutoRepeatCommand;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener {
    private static final Map<String, List<Long>> TIME_STAMP_MAP = new ConcurrentHashMap<>();

    public static void onMessageAdd(MinecraftClient client, Text message) {
        handleAutoRepeat(client, message);
    }

    public static void onClientTick(MinecraftClient client) {
        long currentTime = System.currentTimeMillis();
        TIME_STAMP_MAP.values().forEach(x -> x.removeIf(y -> currentTime - y > 1000));
        TIME_STAMP_MAP.values().removeIf(List::isEmpty);
    }

    private static void handleAutoRepeat(MinecraftClient client, Text message) {
        String originalString = message.getString();
        if (DCCommonConfig.AutoRepeatBlackList.getStrings().stream().anyMatch(originalString::contains)) return;
        DCAutoRepeatCommand.streamTrackedPlayers()
                .map(ChatUtil::angleName)
                .flatMap(x -> ChatUtil.filterMessageContent(x, originalString).stream())
                .max(Comparator.comparing(String::length))
                .ifPresent(x -> sendChatCheckDDos(client, x));
    }

    private static void sendChatCheckDDos(MinecraftClient client, String message) {
        boolean canSend = false;
        if (DCCommonConfig.AutoRepeatAntiDDos.getBooleanValue()) {
            long currentTime = System.currentTimeMillis();
            if (TIME_STAMP_MAP.containsKey(message)) {
                List<Long> list = TIME_STAMP_MAP.get(message);
                if (list.size() < DCCommonConfig.AutoRepeatAntiDDosThreshold.getIntegerValue()) {
                    canSend = true;
                    list.add(currentTime);
                }
            } else {
                List<Long> list = new ArrayList<>();
                list.add(currentTime);
                TIME_STAMP_MAP.put(message, Collections.synchronizedList(list));
                canSend = true;
            }
        } else {
            canSend = true;
        }
        if (canSend) {
            ChatUtil.sendChat(client, message);
        }
    }
}
