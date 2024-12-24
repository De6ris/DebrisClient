package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.command.DCAutoRepeatCommand;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoRepeat {
    private static final Map<String, List<Long>> TIME_STAMP_MAP = new ConcurrentHashMap<>();

    public static void handleAutoRepeat(MinecraftClient client, Text message) {
        String originalString = message.getString();
        switch (((BlackListMode) DCCommonConfig.AutoRepeatBlackListMode.getOptionListValue())) {
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

    public enum BlackListMode implements IConfigOptionListEntry {
        CANCEL("cancel", "取消"),
        REPLACE("replace", "替换"),
        ;

        private final String configString;
        private final String translationKey;

        BlackListMode(String configString, String translationKey) {
            this.configString = configString;
            this.translationKey = translationKey;
        }

        @Override
        public String getStringValue() {
            return this.configString;
        }

        @Override
        public String getDisplayName() {
            return this.translationKey;
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward) {
            int id = this.ordinal();
            if (forward) {
                if (++id >= values().length) {
                    id = 0;
                }
            } else {
                if (--id < 0) {
                    id = values().length - 1;
                }
            }
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String name) {
            return fromStringStatic(name);
        }

        public static BlackListMode fromStringStatic(String name) {
            for (BlackListMode val : values()) {
                if (val.configString.equalsIgnoreCase(name)) {
                    return val;
                }
            }
            return BlackListMode.CANCEL;
        }
    }
}
