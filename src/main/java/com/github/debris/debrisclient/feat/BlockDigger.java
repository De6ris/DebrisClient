package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.util.BlockUtil;
import com.github.debris.debrisclient.util.InteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BlockDigger {
    @Nullable
    private static BlockPos DIG_PROGRESS_POS = null;

    @SuppressWarnings("ConstantConditions")
    public static void digNear(MinecraftClient client, Predicate<BlockState> predicate) {
        BlockPos playerPos = client.player.getBlockPos();
        if (DIG_PROGRESS_POS != null) {// has history
            if (InteractionUtil.withinReach(client, DIG_PROGRESS_POS) && digAreaAndSwingHand(client, Stream.of(DIG_PROGRESS_POS), predicate).isPresent()) {// keep digging
                return;
            } else {// skip or finish
                DIG_PROGRESS_POS = null;
            }
        }
        digAreaAndSwingHand(client, BlockPos.streamOutwards(playerPos, 4, 4, 4), predicate).ifPresent(pos -> DIG_PROGRESS_POS = pos);
    }

    // return a progressing pos
    @SuppressWarnings("ConstantConditions")
    private static Optional<BlockPos> digAreaAndSwingHand(MinecraftClient client, Stream<BlockPos> source, Predicate<BlockState> predicate) {
        AtomicBoolean swingHand = new AtomicBoolean(false);
        Optional<BlockPos> optional = source.filter(pos -> {
                    DigResult digResult = digSingle(client, pos, predicate);
                    if (!digResult.skipped()) swingHand.set(true);
                    return digResult.inProgress();
                })
                .findFirst();
        if (swingHand.get()) client.player.swingHand(Hand.MAIN_HAND);
        return optional;
    }

    @SuppressWarnings("ConstantConditions")
    private static DigResult digSingle(MinecraftClient client, BlockPos pos, Predicate<BlockState> predicate) {
        ClientWorld world = client.world;
        BlockState currentState = world.getBlockState(pos);
        Block block = currentState.getBlock();
        if (!predicate.test(currentState)) return DigResult.SKIP;
        if (shouldSkipDigging(client, world, pos, currentState)) return DigResult.SKIP;
        InteractionUtil.attackBlock(client, pos);
        return world.getBlockState(pos).isOf(block) ? DigResult.PROGRESS : DigResult.FINISH;
    }

    @SuppressWarnings({"ConstantConditions", "RedundantIfStatement"})
    private static boolean shouldSkipDigging(MinecraftClient client, ClientWorld world, BlockPos pos, BlockState state) {
        if (!BlockUtil.canDig(state)) return true;
        if (client.player.isBlockBreakingRestricted(world, pos, client.interactionManager.getCurrentGameMode()))
            return true;
        return false;
    }

    public enum DigResult {
        SKIP,
        PROGRESS,
        FINISH,
        ;

        public boolean skipped() {
            return this == SKIP;
        }

        public boolean inProgress() {
            return this == PROGRESS;
        }

        public boolean finished() {
            return this == FINISH;
        }
    }
}
