package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.unsafe.tweakeroo.RayTraceUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BotUtil {
    @SuppressWarnings("ConstantConditions")
    public static void tryKickBot(MinecraftClient client) {
        if (Predicates.notInGame(client)) return;

        Optional<HitResult> optional = RayTraceUtil.getPlayerRayTrace(client);

        if (optional.isEmpty()) return;

        HitResult trace = optional.get();

        if (trace.getType() != HitResult.Type.ENTITY) return;

        Entity targetedEntity = ((EntityHitResult) trace).getEntity();

        if (targetedEntity instanceof PlayerEntity bot) {

            if (bot.getUuid().equals(client.player.getUuid())) return;// wont kill oneself

            String name = bot.getNameForScoreboard();
            if (inKickQueue(name)) {
                return;
            } else {
                sendKickCommandAndAddToQueue(client.player.networkHandler, name);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean restoreKicking(MinecraftClient client) {
        if (KICK_QUEUE.isEmpty()) {
            return false;
        }
        if (Predicates.notInGame(client)) return false;

        KickEntry last = KICK_QUEUE.getLast();
        sendSpawnCommandAndRemoveFromQueue(client.player.networkHandler, last);
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean suggestBotSpawnCommand(MinecraftClient client) {
        if (Predicates.notInGame(client)) return false;

        Entity camera = client.getCameraEntity();

        String command = String.format("/player bot_ spawn at %.2f %.2f %.2f facing %.2f %.2f in %s",
                camera.getX(),
                camera.getY(),
                camera.getZ(),
                camera.getYaw(),
                camera.getPitch(),
                camera.getWorld().getRegistryKey().getValue());

        ChatScreen chatScreen = new ChatScreen(command);
        client.setScreen(chatScreen);
        TextFieldWidget chatField = chatScreen.chatField;
        chatField.setText(command);
        chatField.setCursor("/player bot_".length(), false);

        return true;
    }

    private static final List<KickEntry> KICK_QUEUE = new ArrayList<>();

    // if false I'll send command and add to kick queue
    private static boolean inKickQueue(String name) {
        Optional<KickEntry> optionalEntry = KICK_QUEUE.stream().filter(x -> x.name.equals(name)).findFirst();
        if (optionalEntry.isEmpty()) return false;
        KickEntry kickEntry = optionalEntry.get();
        long addTime = kickEntry.time;
        long now = System.currentTimeMillis();
        if (now - addTime > 1000L) {// out dated, redo kicking
            KICK_QUEUE.remove(kickEntry);
            return false;
        }
        return true;
    }

    private static void sendKickCommandAndAddToQueue(ClientPlayNetworkHandler handler, String name) {
        handler.sendChatCommand(String.format("player %s kill", name));
        KICK_QUEUE.add(new KickEntry(name, System.currentTimeMillis()));
    }

    private static void sendSpawnCommandAndRemoveFromQueue(ClientPlayNetworkHandler handler, KickEntry entry) {
        handler.sendChatCommand(String.format("player %s spawn", entry.name));
        KICK_QUEUE.remove(entry);
    }

    private record KickEntry(String name, long time) {
    }
}
