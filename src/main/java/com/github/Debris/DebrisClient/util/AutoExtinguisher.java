package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Predicate;

public class AutoExtinguisher {
    public static void onClientTick(MinecraftClient client) {
        if (!DCCommonConfig.AutoExtinguisher.getBooleanValue()) return;
        if (Predicates.notInGame(client)) return;
        Predicate<BlockState> fireTest = state -> state.isOf(Blocks.FIRE);
        digNear(client, fireTest);
    }

    private static BlockPos TEMP_POS = null;

    @SuppressWarnings("ConstantConditions")
    private static void digNear(MinecraftClient client, Predicate<BlockState> predicate) {
        if (TEMP_POS != null) {// has history
            if (digPredicate(client, TEMP_POS, predicate)) {// keep digging
                return;
            } else {// dig finish
                TEMP_POS = null;
            }
        }
        Optional<BlockPos> optional = PositionUtil.streamNear3D(client.player.getBlockPos(), 3).filter(pos -> digPredicate(client, pos, predicate)).findFirst();
        optional.ifPresent(pos -> TEMP_POS = pos);
    }

    // false: skip or success, true: keep digging
    @SuppressWarnings("ConstantConditions")
    private static boolean digPredicate(MinecraftClient client, BlockPos pos, Predicate<BlockState> predicate) {
        ClientWorld world = client.world;
        BlockState currentState = world.getBlockState(pos);
        Block block = currentState.getBlock();
        if (!predicate.test(currentState)) return false;
        if (shouldSkipDigging(client, world, pos, currentState)) return false;
        client.interactionManager.updateBlockBreakingProgress(pos, Direction.DOWN);
        client.player.swingHand(Hand.MAIN_HAND);
        client.interactionManager.cancelBlockBreaking();
        return world.getBlockState(pos).isOf(block);
    }

    @SuppressWarnings("ConstantConditions")
    private static boolean shouldSkipDigging(MinecraftClient client, ClientWorld world, BlockPos pos, BlockState state) {
        if (state.isAir()) return true;
        if (state.isOf(Blocks.AIR)) return true;
        if (state.isOf(Blocks.CAVE_AIR)) return true;
        if (state.isOf(Blocks.VOID_AIR)) return true;
        Block block = state.getBlock();
        if (block.getHardness() == -1.0F) return true;
        if (block instanceof FluidBlock) return true;
        if (client.player.isBlockBreakingRestricted(world, pos, client.interactionManager.getCurrentGameMode()))
            return true;
        return false;
    }
}
