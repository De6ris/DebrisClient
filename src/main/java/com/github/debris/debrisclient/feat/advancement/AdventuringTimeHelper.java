package com.github.debris.debrisclient.feat.advancement;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.unsafe.XaeroMiniMapAccess;
import com.github.debris.debrisclient.util.ChatUtil;
import com.github.debris.debrisclient.util.StringUtil;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.impl.event.lifecycle.LoadedChunksCache;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AdventuringTimeHelper {
    private static final Identifier ADVANCEMENT_ID = Identifier.of("adventure/adventuring_time");

    private static final Logger LOGGER = LoggerFactory.getLogger(AdventuringTimeHelper.class);

    private static final int MAX_PROCESS = 5;

    private static final Set<Biome> PENDING_BIOMES = new HashSet<>();

    private static final Deque<Long> CHUNK_QUEUE = new ArrayDeque<>();

    private static final LongSet EXISTING_CHUNKS = new LongOpenHashSet();

    private static final Set<Biome> GLOWING_BIOMES = Collections.synchronizedSet(new HashSet<>());

    public static boolean isActive() {
        return DCCommonConfig.AdventuringTimeHelper.getBooleanValue() && (ModReference.hasMod(ModReference.XaeroMiniMap) || ModReference.hasMod(ModReference.BetterPvP));
    }

    public static void onChunkLoad(ClientWorld world, WorldChunk chunk) {
        if (!isActive()) return;
        if (!progressAware()) return;
        if (!isOverworld(world)) return;
        ChunkPos chunkPos = chunk.getPos();
        if (!newChunk(chunkPos)) return;
        addToQueue(chunkPos);
    }

    public static void onChunkUnload(ClientWorld world, WorldChunk chunk) {
        CHUNK_QUEUE.remove(chunk.getPos().toLong());
    }

    private static void addToQueue(ChunkPos chunkPos) {
        CHUNK_QUEUE.push(chunkPos.toLong());
    }

    private static boolean isOverworld(World world) {
        return world.getDimension().natural();
    }

    private static boolean newChunk(ChunkPos chunkPos) {
        long l = chunkPos.toLong();
        return !EXISTING_CHUNKS.contains(l) && !CHUNK_QUEUE.contains(l);
    }

    private static boolean pending(Biome biome) {
        return PENDING_BIOMES.contains(biome);
    }

    private static void glow(Biome biome, BlockPos blockPos) {
        GLOWING_BIOMES.add(biome);
        String name = StringUtil.translateBiome(biome);
        XaeroMiniMapAccess.addDestination(blockPos, name);
        ChatUtil.addLocalMessageNextTick(Text.literal(name));
    }

    private static boolean glowing(Biome biome) {
        return GLOWING_BIOMES.contains(biome);
    }

    public static void onProgressUpdate(MinecraftClient client, Map<Identifier, AdvancementProgress> map) {
        if (!isActive()) return;
        Identifier id = ADVANCEMENT_ID;
        if (!map.containsKey(id)) return;
        AdvancementProgress progress = map.get(id);
        readProgress(client, progress);
    }

    public static void onWorldLoad(MinecraftClient client) {
        clear();
    }

    public static void onConfigChange(MinecraftClient client) {
        if (isActive()) {
            updateProgress(client);
            ClientWorld world = client.world;
            for (WorldChunk chunk : ((LoadedChunksCache) world).fabric_getLoadedChunks()) {
                onChunkLoad(world, chunk);
            }
        }
    }

    private static boolean progressAware() {
        return !PENDING_BIOMES.isEmpty();
    }

    private static void updateProgress(MinecraftClient client) {
        ClientAdvancementManager manager = client.getNetworkHandler().getAdvancementHandler();
        manager.setListener(new ProgressCollector());
        manager.setListener(null);
    }

    private static void readProgress(MinecraftClient client, AdvancementProgress progress) {
        Registry<Biome> registry = client.world.getRegistryManager().getOrThrow(RegistryKeys.BIOME);
        Set<Biome> set = PENDING_BIOMES;
        set.clear();
        for (String s : progress.getUnobtainedCriteria()) {
            Identifier identifier = Identifier.of(s);
            Biome biome = registry.get(identifier);
            if (biome == null) {
                LOGGER.warn("no biome for key {}", s);
                continue;
            }
            set.add(biome);
        }
    }

    public static void onClientTick(MinecraftClient client) {
        if (!isActive()) return;
        ClientWorld world = client.world;
        if (world == null) return;
        if (!isOverworld(world)) return;
        Deque<Long> queue = CHUNK_QUEUE;
        int process = 0;
        while (!queue.isEmpty() && process < MAX_PROCESS) {
            long pop = queue.pop();
            CompletableFuture.runAsync(() -> process(world, new ChunkPos(pop)));
            EXISTING_CHUNKS.add(pop);
            process++;
        }
    }

    private static void process(World world, ChunkPos chunkPos) {
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        BlockPos.Mutable blockPos = new BlockPos.Mutable();
        BiomeAccess biomeAccess = world.getBiomeAccess();
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = startX + dx;
                int z = startZ + dz;
                int topY = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
                blockPos.set(x, topY, z);
                RegistryEntry<Biome> registryEntry = biomeAccess.getBiome(blockPos);
                Biome biome = registryEntry.value();
                if (!pending(biome)) continue;
                if (glowing(biome)) continue;
                glow(biome, blockPos);
            }
        }
    }

    private static void clear() {
        CHUNK_QUEUE.clear();
        EXISTING_CHUNKS.clear();
        GLOWING_BIOMES.clear();
    }

    private static class ProgressCollector implements ClientAdvancementManager.Listener {
        @Override
        public void setProgress(PlacedAdvancement advancement, AdvancementProgress progress) {
            if (advancement.getAdvancementEntry().id().equals(ADVANCEMENT_ID)) {
                readProgress(MinecraftClient.getInstance(), progress);
            }
        }

        @Override
        public void selectTab(@Nullable AdvancementEntry advancement) {

        }

        @Override
        public void onRootAdded(PlacedAdvancement root) {

        }

        @Override
        public void onRootRemoved(PlacedAdvancement root) {

        }

        @Override
        public void onDependentAdded(PlacedAdvancement dependent) {

        }

        @Override
        public void onDependentRemoved(PlacedAdvancement dependent) {

        }

        @Override
        public void onClear() {

        }
    }
}
