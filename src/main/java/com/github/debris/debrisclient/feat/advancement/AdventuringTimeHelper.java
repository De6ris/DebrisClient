package com.github.debris.debrisclient.feat.advancement;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.unsafe.XaeroMiniMapAccess;
import com.github.debris.debrisclient.util.ChatUtil;
import com.github.debris.debrisclient.util.StringUtil;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.impl.event.lifecycle.LoadedChunksCache;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AdventuringTimeHelper {
    private static final Identifier ADVANCEMENT_ID = Identifier.parse("adventure/adventuring_time");

    private static final Logger LOGGER = LoggerFactory.getLogger(AdventuringTimeHelper.class);

    private static final int MAX_PROCESS = 5;

    private static final Set<Biome> PENDING_BIOMES = new HashSet<>();

    private static final Deque<Long> CHUNK_QUEUE = new ArrayDeque<>();

    private static final LongSet EXISTING_CHUNKS = new LongOpenHashSet();

    private static final Set<Biome> GLOWING_BIOMES = Collections.synchronizedSet(new HashSet<>());

    public static boolean isActive() {
        return DCCommonConfig.AdventuringTimeHelper.getBooleanValue() && (ModReference.hasMod(ModReference.XaeroMiniMap) || ModReference.hasMod(ModReference.BetterPvP));
    }

    public static void onChunkLoad(ClientLevel world, LevelChunk chunk) {
        if (!isActive()) return;
        if (!progressAware()) return;
        if (!isOverworld(world)) return;
        ChunkPos chunkPos = chunk.getPos();
        if (!newChunk(chunkPos)) return;
        addToQueue(chunkPos);
    }

    public static void onChunkUnload(ClientLevel world, LevelChunk chunk) {
        CHUNK_QUEUE.remove(chunk.getPos().toLong());
    }

    private static void addToQueue(ChunkPos chunkPos) {
        CHUNK_QUEUE.push(chunkPos.toLong());
    }

    private static boolean isOverworld(Level world) {
        return world.dimension() == Level.OVERWORLD;
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
        ChatUtil.addLocalMessageNextTick(Component.literal(name));
    }

    private static boolean glowing(Biome biome) {
        return GLOWING_BIOMES.contains(biome);
    }

    public static void onProgressUpdate(Minecraft client, Map<Identifier, AdvancementProgress> map) {
        if (!isActive()) return;
        Identifier id = ADVANCEMENT_ID;
        if (!map.containsKey(id)) return;
        AdvancementProgress progress = map.get(id);
        readProgress(client, progress);
    }

    public static void onWorldLoad(Minecraft client) {
        clear();
    }

    public static void onConfigChange(Minecraft client) {
        if (isActive()) {
            updateProgress(client);
            ClientLevel world = client.level;
            for (LevelChunk chunk : ((LoadedChunksCache) world).fabric_getLoadedChunks()) {
                onChunkLoad(world, chunk);
            }
        }
    }

    private static boolean progressAware() {
        return !PENDING_BIOMES.isEmpty();
    }

    private static void updateProgress(Minecraft client) {
        ClientAdvancements manager = client.getConnection().getAdvancements();
        manager.setListener(new ProgressCollector());
        manager.setListener(null);
    }

    private static void readProgress(Minecraft client, AdvancementProgress progress) {
        Registry<Biome> registry = client.level.registryAccess().lookupOrThrow(Registries.BIOME);
        Set<Biome> set = PENDING_BIOMES;
        set.clear();
        for (String s : progress.getRemainingCriteria()) {
            Identifier identifier = Identifier.parse(s);
            Biome biome = registry.getValue(identifier);
            if (biome == null) {
                LOGGER.warn("no biome for key {}", s);
                continue;
            }
            set.add(biome);
        }
    }

    public static void onClientTick(Minecraft client) {
        if (!isActive()) return;
        ClientLevel world = client.level;
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

    private static void process(Level world, ChunkPos chunkPos) {
        int startX = chunkPos.getMinBlockX();
        int startZ = chunkPos.getMinBlockZ();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        BiomeManager biomeAccess = world.getBiomeManager();
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = startX + dx;
                int z = startZ + dz;
                int topY = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                blockPos.set(x, topY, z);
                Holder<Biome> registryEntry = biomeAccess.getBiome(blockPos);
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

    private static class ProgressCollector implements ClientAdvancements.Listener {
        @Override
        public void onUpdateAdvancementProgress(AdvancementNode advancement, AdvancementProgress progress) {
            if (advancement.holder().id().equals(ADVANCEMENT_ID)) {
                readProgress(Minecraft.getInstance(), progress);
            }
        }

        @Override
        public void onSelectedTabChanged(@Nullable AdvancementHolder advancement) {

        }

        @Override
        public void onAddAdvancementRoot(AdvancementNode root) {

        }

        @Override
        public void onRemoveAdvancementRoot(AdvancementNode root) {

        }

        @Override
        public void onAddAdvancementTask(AdvancementNode dependent) {

        }

        @Override
        public void onRemoveAdvancementTask(AdvancementNode dependent) {

        }

        @Override
        public void onAdvancementsCleared() {

        }
    }
}
