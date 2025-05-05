package com.github.debris.debrisclient.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtil {
    @SuppressWarnings("RedundantIfStatement")
    public static boolean canDig(BlockState state) {
        if (state.isAir()) return false;
        Block block = state.getBlock();
        if (block.getHardness() == -1.0F) return false;
        if (block instanceof FluidBlock) return false;
        return true;
    }

    public static boolean isContainer(World world, BlockPos pos) {
        return world.getBlockState(pos).hasBlockEntity() && world.getChunk(pos).getBlockEntity(pos) instanceof ScreenHandlerFactory;
    }
}
