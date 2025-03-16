package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.command.DCAutoRepeatCommand;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoRepeat {
    private static final Map<String, List<Long>> TIME_STAMP_MAP = new ConcurrentHashMap<>();

    public static void handleAutoRepeat(MinecraftClient client, Text message) {
        String originalString = message.getString();
        switch (DCCommonConfig.AutoRepeatBlackListMode.getEnumValue()) {
            case CANCEL -> {
                if (DCCommonConfig.AutoRepeatBlackList.getStrings().stream().anyMatch(originalString::contains)) return;
            }
            case REPLACE -> {
                String replace = DCCommonConfig.AutoRepeatBlackListReplace.getStringValue();
                for (String word : DCCommonConfig.AutoRepeatBlackList.getStrings()) {
                    originalString = originalString.replace(word, replace);
                }
            }
        }

        final String finalString = originalString;

        DCAutoRepeatCommand.streamTrackedPlayers()
                .map(ChatUtil::angleName)
                .flatMap(x -> ChatUtil.filterMessageContent(x, finalString).stream())
                .max(Comparator.comparing(String::length))
                .ifPresent(x -> sendChatCheckDDos(client, x));
    }

    public static void onClientTick(MinecraftClient client) {
        if (TIME_STAMP_MAP.isEmpty()) return;
        long currentTime = System.currentTimeMillis();
        TIME_STAMP_MAP.values().forEach(x -> x.removeIf(y -> currentTime - y > 1000));
        TIME_STAMP_MAP.values().removeIf(List::isEmpty);
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

    public enum BlackListMode {
        CANCEL,
        REPLACE,
        ;
    }
}
