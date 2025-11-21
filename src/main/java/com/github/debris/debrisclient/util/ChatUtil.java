package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.feat.FutureTaskQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

public class ChatUtil {
    @SuppressWarnings("ConstantConditions")
    public static void sendChat(Minecraft client, String content) {
        content = StringUtil.trimChatMessage(StringUtils.normalizeSpace(content.trim()));
        if (content.isEmpty()) return;
        if (content.startsWith("/")) {
            client.player.connection.sendCommand(content.substring(1));
        } else {
            client.player.connection.sendChat(content);
        }
    }

    public static void addLocalMessage(Component message) {
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }

    public static void addLocalMessageNextTick(Component message) {
        FutureTaskQueue.addNextTick(() -> addLocalMessage(message));
    }
}
