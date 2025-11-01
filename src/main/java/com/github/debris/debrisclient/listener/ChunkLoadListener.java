package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.feat.advancement.AdventuringTimeHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkLoadListener {
    public static void onChunkLoad(ClientWorld world, WorldChunk chunk) {
        AdventuringTimeHelper.onChunkLoad(world, chunk);
    }

    public static void onChunkUnload(ClientWorld world, WorldChunk chunk) {
        AdventuringTimeHelper.onChunkUnload(world, chunk);
    }
}
