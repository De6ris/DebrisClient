package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.util.AccessorUtil;
import com.github.debris.debrisclient.util.ChatUtil;
import com.github.debris.debrisclient.util.CollectingCharacterVisitor;
import com.github.debris.debrisclient.util.Predicates;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ResendChat {
    @SuppressWarnings("ConstantConditions")
    public static boolean resendLast(Minecraft client) {
        if (Predicates.notInGame(client)) return false;
        String lastChat = client.gui.getChat().getRecentChat().peekLast();
        if (lastChat != null) {
            ChatUtil.sendChat(client, lastChat);
            return true;
        }
        return false;
    }

    public static boolean repeatNewestChat(Minecraft client) {
        if (Predicates.notInGame(client)) return false;
        List<GuiMessage.Line> visibleMessages = AccessorUtil.getVisibleMessages(client.gui.getChat());
        if (visibleMessages.isEmpty()) return false;
        List<GuiMessage.Line> parts = new ArrayList<>();
        parts.add(visibleMessages.getFirst());
        for (int i = 1; i < visibleMessages.size(); i++) {
            GuiMessage.Line visible = visibleMessages.get(i);
            if (visible.endOfEntry()) break;
            parts.add(visible);// message may be broken into lines
        }
        // in the original list, newest line lies on the first, so we reverse the order
        Optional<String> optional = checkSentByPlayer(client, convertString(parts.reversed()));
        optional.ifPresent(x -> ChatUtil.sendChat(client, x));
        return optional.isPresent();
    }

    @SuppressWarnings("ConstantConditions")
    private static Optional<String> checkSentByPlayer(Minecraft client, String original) {
        return client.player.connection.getListedOnlinePlayers().stream()
                .map(PlayerInfo::getProfile)
                .map(GameProfile::name)
                .flatMap(name -> filterAndCut(name, original).stream())
                .max(Comparator.comparing(String::length));
    }

    private static Optional<String> filterAndCut(String senderName, String original) {
        String senderNameAngled = "<" + senderName + ">";
        int index = original.indexOf(senderNameAngled);
        if (index == -1) return Optional.empty();
        return Optional.of(original.substring(index + senderNameAngled.length()));
    }

    private static String convertString(List<GuiMessage.Line> lines) {
        CollectingCharacterVisitor visitor = new CollectingCharacterVisitor();
        for (GuiMessage.Line line : lines) {
            line.content().accept(visitor);
        }
        return visitor.collect();
    }
}
