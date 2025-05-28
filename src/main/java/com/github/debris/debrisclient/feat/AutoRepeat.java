package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoRepeat {
    private static List<BlackListPattern> PATTERNS = compile(DCCommonConfig.AutoRepeatBlackList.getStrings());
    private static final Map<String, List<Long>> TIME_STAMP_MAP = new ConcurrentHashMap<>();

    public static void handleAutoRepeat(MinecraftClient client, Text message) {
        List<String> playerNames = DCCommonConfig.AutoRepeatPlayerList.getStrings();
        if (playerNames.isEmpty()) return;

        String mutable = message.getString();
        for (BlackListPattern pattern : PATTERNS) {
            switch (pattern.getMode()) {
                case CANCEL -> {
                    if (mutable.contains(((BlackListPattern.Cancel) pattern).string())) return;
                }
                case REPLACE -> {
                    BlackListPattern.Replace replace = (BlackListPattern.Replace) pattern;
                    mutable = mutable.replace(replace.input(), replace.output());
                }
            }
        }

        final String finalString = mutable;

        playerNames.stream()
                .flatMap(name -> filterAndCut(name, finalString).stream())
                .max(Comparator.comparing(String::length))
                .ifPresent(x -> sendChatCheckDDos(client, x));
    }

    public static void onClientTick(MinecraftClient client) {
        if (TIME_STAMP_MAP.isEmpty()) return;
        long currentTime = System.currentTimeMillis();
        TIME_STAMP_MAP.values().forEach(x -> x.removeIf(y -> currentTime - y > 1000));
        TIME_STAMP_MAP.values().removeIf(List::isEmpty);
    }

    private static Optional<String> filterAndCut(String senderName, String original) {
        String senderNameAngled = "<" + senderName + ">";
        int index = original.indexOf(senderNameAngled);
        if (index == -1) return Optional.empty();
        return Optional.of(original.substring(index + senderNameAngled.length()));
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

    public static void updateBlackList(List<String> strings) {
        PATTERNS = compile(strings);
    }

    private static List<BlackListPattern> compile(List<String> strings) {
        return strings.stream().map(BlackListPattern::compile).toList();
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
