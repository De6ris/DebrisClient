package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.command.DCAutoRepeatCommand;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoRepeat {
    private static final Map<String, List<Long>> TIME_STAMP_MAP = new ConcurrentHashMap<>();

    public static void handleAutoRepeat(MinecraftClient client, Text message) {
        String originalString = message.getString();
        List<BlackListPattern> patterns = DCCommonConfig.AutoRepeatBlackList.getStrings().stream().map(BlackListPattern::compile).toList();
        for (BlackListPattern pattern : patterns) {
            switch (pattern.getMode()) {
                case CANCEL -> {
                    if (originalString.contains(((BlackListPattern.Cancel) pattern).string())) return;
                }
                case REPLACE -> {
                    BlackListPattern.Replace replace = (BlackListPattern.Replace) pattern;
                    originalString = originalString.replace(replace.input(), replace.output());
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
        int threshold = DCCommonConfig.AutoRepeatAntiDDos.getIntegerValue();
        if (threshold == Integer.MAX_VALUE) {
            canSend = true;
        } else {
            long currentTime = System.currentTimeMillis();
            if (TIME_STAMP_MAP.containsKey(message)) {
                List<Long> list = TIME_STAMP_MAP.get(message);
                if (list.size() < threshold) {
                    canSend = true;
                    list.add(currentTime);
                }
            } else {
                List<Long> list = new ArrayList<>();
                list.add(currentTime);
                TIME_STAMP_MAP.put(message, Collections.synchronizedList(list));
                canSend = true;
            }
        }
        if (canSend) {
            ChatUtil.sendChat(client, message);
        }
    }

    public enum BlackListMode {
        NONE,
        CANCEL,
        REPLACE,
        ;
    }

    private interface BlackListPattern {
        BlackListPattern NONE = () -> BlackListMode.NONE;

        BlackListMode getMode();

        static BlackListPattern compile(String string) {
            if (string.isEmpty()) return NONE;
            if (string.contains("->")) {
                String[] split = string.split("[->]");
                if (split.length != 2) return NONE;
                return new Replace(split[0], split[1]);
            }
            return new Cancel(string);
        }

        record Cancel(String string) implements BlackListPattern {
            @Override
            public BlackListMode getMode() {
                return BlackListMode.CANCEL;
            }
        }

        record Replace(String input, String output) implements BlackListPattern {
            @Override
            public BlackListMode getMode() {
                return BlackListMode.REPLACE;
            }
        }
    }
}
