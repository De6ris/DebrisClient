package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.util.InteractionUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class BlockInteractor {
    private static final Collection<BlockPos> TARGETS = new HashSet<>();

    public static boolean running() {
        return !TARGETS.isEmpty();
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
            TARGETS.remove(blockPos);
            InfoUtils.printActionbarMessage("方块交互: 还剩" + TARGETS.size() + "处");
        }
    }
}
