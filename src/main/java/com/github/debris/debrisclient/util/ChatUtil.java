package com.github.debris.debrisclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.apache.commons.lang3.StringUtils;

public class ChatUtil {
    @SuppressWarnings("ConstantConditions")
    public static void sendChat(MinecraftClient client, String content) {
        content = StringHelper.truncateChat(StringUtils.normalizeSpace(content.trim()));
        if (content.isEmpty()) return;
        if (content.startsWith("/")) {
            client.player.networkHandler.sendChatCommand(content.substring(1));
        } else {
            client.player.networkHandler.sendChatMessage(content);
        }
    }

    public static void addLocalMessage(Text message) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
    }

}
