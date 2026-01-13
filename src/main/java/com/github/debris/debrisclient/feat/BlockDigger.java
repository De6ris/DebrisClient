package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.util.BlockUtil;
import com.github.debris.debrisclient.util.InteractionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BlockDigger {
    @Nullable
    private static BlockPos DIG_PROGRESS_POS = null;

    @SuppressWarnings("ConstantConditions")
    public static void digNear(Minecraft client, Predicate<BlockState> predicate) {
        BlockPos playerPos = client.player.blockPosition();
        if (DIG_PROGRESS_POS != null) {// has history
            if (InteractionUtil.withinReach(client, DIG_PROGRESS_POS) && digAreaAndSwingHand(client, Stream.of(DIG_PROGRESS_POS), predicate).isPresent()) {// keep digging
                return;
            } else {// skip or finish
                DIG_PROGRESS_POS = null;
            }
        }
        digAreaAndSwingHand(client, BlockPos.withinManhattanStream(playerPos, 4, 4, 4), predicate).ifPresent(pos -> DIG_PROGRESS_POS = pos);
    }

    // return a progressing pos
    @SuppressWarnings("ConstantConditions")
    private static Optional<BlockPos> digAreaAndSwingHand(Minecraft client, Stream<BlockPos> source, Predicate<BlockState> predicate) {
        AtomicBoolean swingHand = new AtomicBoolean(false);
        Optional<BlockPos> optional = source.filter(pos -> {
                    DigResult digResult = digSingle(client, pos, predicate);
                    if (!digResult.skipped()) swingHand.set(true);
                    return digResult.inProgress();
                })
                .findFirst();
        if (swingHand.get()) client.player.swing(InteractionHand.MAIN_HAND);
        return optional;
    }

    @SuppressWarnings("ConstantConditions")
    private static DigResult digSingle(Minecraft client, BlockPos pos, Predicate<BlockState> predicate) {
        ClientLevel world = client.level;
        BlockState currentState = world.getBlockState(pos);
        Block block = currentState.getBlock();
        if (!predicate.test(currentState)) return DigResult.SKIP;
        if (shouldSkipDigging(client, world, pos, currentState)) return DigResult.SKIP;
        InteractionUtil.attackBlock(client, pos);
        return world.getBlockState(pos).is(block) ? DigResult.PROGRESS : DigResult.FINISH;
    }

    @SuppressWarnings({"ConstantConditions", "RedundantIfStatement"})
    private static boolean shouldSkipDigging(Minecraft client, ClientLevel world, BlockPos pos, BlockState state) {
        if (!BlockUtil.canDig(state)) return true;
        if (client.player.blockActionRestricted(world, pos, client.gameMode.getPlayerMode()))
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
