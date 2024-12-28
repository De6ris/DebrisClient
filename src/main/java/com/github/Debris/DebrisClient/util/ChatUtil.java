package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.unsafe.tweakeroo.RayTraceUtil;
import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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
        return client.player.networkHandler.getListedPlayerListEntries().stream()
                .map(PlayerListEntry::getProfile)
                .map(GameProfile::getName)
                .map(ChatUtil::angleName)
                .flatMap(x -> filterMessageContent(x, original).stream())
                .max(Comparator.comparing(String::length));
    }

    public static String angleName(String rawName) {
        return "<" + rawName + ">";
    }

    public static Optional<String> filterMessageContent(String senderNameAngled, String original) {
        int index = original.indexOf(senderNameAngled);
        if (index == -1) return Optional.empty();
        return Optional.of(original.substring(index + senderNameAngled.length()));
    }

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

    public static String convertString(List<ChatHudLine.Visible> lines) {
        CollectingCharacterVisitor visitor = new CollectingCharacterVisitor();
        for (ChatHudLine.Visible line : lines) {
            line.content().accept(visitor);
        }
        return visitor.collect();
    }

    public static boolean dataGet(MinecraftClient client) {
        if (Predicates.notInGame(client)) return false;
        if (FabricLoader.getInstance().isModLoaded("tweakeroo")) {
            Optional<HitResult> optional = RayTraceUtil.getPlayerRayTrace(client);
            if (optional.isEmpty()) return false;
            HitResult trace = optional.get();
            switch (trace.getType()) {
                case BLOCK -> {
                    BlockPos blockPos = ((BlockHitResult) trace).getBlockPos();
                    ChatUtil.sendChat(client, String.format("/data get block %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    return true;
                }
                case ENTITY -> {
                    UUID uuid = ((EntityHitResult) trace).getEntity().getUuid();
                    ChatUtil.sendChat(client, String.format("/data get entity %s", uuid));
                    return true;
                }
            }
        }
        return false;
    }

    public static void addLocalMessage(Text message) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
    }

}
