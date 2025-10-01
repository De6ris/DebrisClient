package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.util.AccessorUtil;
import com.github.debris.debrisclient.util.ChatUtil;
import com.github.debris.debrisclient.util.Predicates;
import com.github.debris.debrisclient.util.RayTraceUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarpetBot {
    @SuppressWarnings({"ConstantConditions", "UnnecessaryReturnStatement"})
    public static void tryKickBot(MinecraftClient client) {
        if (Predicates.notInGame(client)) return;

        Optional<Entity> optionalEntity = RayTraceUtil.getRayTraceEntity(client);
        if (optionalEntity.isEmpty()) return;
        Entity entity = optionalEntity.get();

        if (entity instanceof PlayerEntity bot) {

            if (bot.getUuid().equals(client.player.getUuid())) return;// wont kill oneself

            String name = bot.getNameForScoreboard();
            if (inKickQueue(name)) {
                return;
            } else {
                sendKickCommandAndAddToQueue(client, name, bot);
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
        sendSpawnCommandAndRemoveFromQueue(client, last);
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean suggestBotSpawnCommand(MinecraftClient client) {
        if (Predicates.notInGame(client)) return false;

        String command = SpawnContext.fromEntity(client.getCameraEntity()).getSpawnCommand("bot_");

        ChatScreen chatScreen = new ChatScreen(command, false);
        client.setScreen(chatScreen);
        TextFieldWidget chatField = AccessorUtil.getChatField(chatScreen);
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

    private static void sendKickCommandAndAddToQueue(MinecraftClient client, String name, Entity bot) {
        ChatUtil.sendChat(client, String.format("/player %s kill", name));
        KICK_QUEUE.add(new KickEntry(name, System.currentTimeMillis(), SpawnContext.fromEntity(bot)));
    }

    private static void sendSpawnCommandAndRemoveFromQueue(MinecraftClient client, KickEntry entry) {
        ChatUtil.sendChat(client, entry.context.getSpawnCommand(entry.name));
        KICK_QUEUE.remove(entry);
    }

    private record KickEntry(String name, long time, SpawnContext context) {
    }

    private record SpawnContext(double x, double y, double z, float yaw, float pitch, String dimension) {
        static SpawnContext fromEntity(Entity entity) {
            return new SpawnContext(entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    entity.getYaw(),
                    entity.getPitch(),
                    entity.getEntityWorld().getRegistryKey().getValue().toString()
            );
        }

        String getSpawnCommand(String name) {
            return String.format("/player %s spawn at %.2f %.2f %.2f facing %.2f %.2f in %s", name, x, y, z, yaw, pitch, dimension);
        }
    }
}
