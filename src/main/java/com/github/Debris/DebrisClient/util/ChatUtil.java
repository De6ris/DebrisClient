package com.github.Debris.DebrisClient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatUtil {
    @SuppressWarnings("ConstantConditions")
    public static boolean resendLast(MinecraftClient client) {
        String lastChat = client.inGameHud.getChatHud().getMessageHistory().peekLast();
        if (lastChat != null) {
            sendChat(client, lastChat);
            return true;
        }
        return false;
    }

    public static boolean repeatNewestChat(MinecraftClient client) {
        List<ChatHudLine.Visible> visibleMessages = AccessorUtil.getVisibleMessages(client.inGameHud.getChatHud());
        if (visibleMessages.isEmpty()) return false;
        List<ChatHudLine.Visible> parts = new ArrayList<>();
        parts.add(visibleMessages.getFirst());
        for (int i = 1; i < visibleMessages.size(); i++) {
            ChatHudLine.Visible visible = visibleMessages.get(i);
            if (visible.endOfEntry()) break;
            parts.add(visible);// message may be broken into lines
        }
        // in the original list, newest line lies on the first, so we reverse the order
        Optional<String> optional = checkSentByPlayer(client, convertString(parts.reversed()));
        optional.ifPresent(x -> sendChat(client, x));
        return optional.isPresent();
    }

    @SuppressWarnings("ConstantConditions")
    private static Optional<String> checkSentByPlayer(MinecraftClient client, String original) {
        for (PlayerListEntry listedPlayerListEntry : client.player.networkHandler.getListedPlayerListEntries()) {
            String name = listedPlayerListEntry.getProfile().getName();
            String angled = "<" + name + ">";
            int index = original.indexOf(angled);
            if (index != -1) {
                return Optional.of(original.substring(index + angled.length()).trim());
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantConditions")
    public static void sendChat(MinecraftClient client, String content) {
        if (content.startsWith("/")) {
            client.player.networkHandler.sendChatCommand(content.substring(1));
        } else {
            client.player.networkHandler.sendChatMessage(content);
        }
    }

    public static String convertString(List<ChatHudLine.Visible> lines) {
        CollectingCharacterVisitor visitor = new CollectingCharacterVisitor();
        for (ChatHudLine.Visible line : lines) {
            line.content().accept(visitor);
        }
        return visitor.collect();
    }

}
