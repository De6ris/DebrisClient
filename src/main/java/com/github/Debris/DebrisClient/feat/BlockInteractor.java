package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.render.RenderQueue;
import com.github.Debris.DebrisClient.render.RendererFactory;
import com.github.Debris.DebrisClient.util.InteractionUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class BlockInteractor {
    private static final Collection<BlockPos> TARGETS = new HashSet<>();

    public static boolean running() {
        return !TARGETS.isEmpty();
    }

    public static int size() {
        return TARGETS.size();
    }

    public static void stop() {
        TARGETS.clear();
    }

    public static void add(BlockPos pos) {
        TARGETS.add(pos);
    }

    public static void addAll(Collection<BlockPos> list) {
        TARGETS.addAll(list);
    }

    public static void onClientTick(MinecraftClient client) {
        if (!Predicates.inGameNoGui(client)) return;
        if (TARGETS.isEmpty()) return;
        Optional<BlockPos> optional = TARGETS.stream().filter(pos -> InteractionUtil.withinReach(client, pos)).findFirst();
        if (optional.isPresent()) {
            BlockPos blockPos = optional.get();
            InteractionUtil.interactBlock(client, blockPos);
            if (Predicates.hasMod(ModReference.MagicLibMCApi)) {
                RenderQueue.add(RendererFactory.text(Text.literal("已交互"), blockPos), 100);
            }
            TARGETS.remove(blockPos);
        }
    }
}
