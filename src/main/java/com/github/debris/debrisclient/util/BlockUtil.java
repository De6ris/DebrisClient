package com.github.debris.debrisclient.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUtil {
    @SuppressWarnings("RedundantIfStatement")
    public static boolean canDig(BlockState state) {
        if (state.isAir()) return false;
        Block block = state.getBlock();
        if (block.defaultDestroyTime() == -1.0F) return false;
        if (block instanceof LiquidBlock) return false;
        return true;
    }

    public static boolean isContainer(Level world, BlockPos pos) {
        return world.getBlockState(pos).hasBlockEntity() && world.getChunk(pos).getBlockEntity(pos) instanceof MenuConstructor;
    }
}
