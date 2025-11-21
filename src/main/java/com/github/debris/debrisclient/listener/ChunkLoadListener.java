package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.feat.advancement.AdventuringTimeHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkLoadListener {
    public static void onChunkLoad(ClientLevel world, LevelChunk chunk) {
        AdventuringTimeHelper.onChunkLoad(world, chunk);
    }

    public static void onChunkUnload(ClientLevel world, LevelChunk chunk) {
        AdventuringTimeHelper.onChunkUnload(world, chunk);
    }
}
