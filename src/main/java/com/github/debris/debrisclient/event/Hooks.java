package com.github.debris.debrisclient.event;

import com.github.debris.debrisclient.feat.AutoRepeat;
import com.github.debris.debrisclient.feat.advancement.AdventuringTimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.chunk.LevelChunk;

public class Hooks {
    public static void onTradeInfoUpdate(Minecraft client) {
    }

    public static void onMessageAdd(Minecraft client, Component message) {
        AutoRepeat.handleAutoRepeat(client, message);
    }

    public static void onChunkLoad(ClientLevel world, LevelChunk chunk) {
        AdventuringTimeHelper.onChunkLoad(world, chunk);
    }

    public static void onChunkUnload(ClientLevel world, LevelChunk chunk) {
        AdventuringTimeHelper.onChunkUnload(world, chunk);
    }
}
