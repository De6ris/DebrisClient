package com.github.Debris.DebrisClient.util;

import net.minecraft.util.math.BlockPos;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PositionUtil {
    public static Stream<BlockPos> streamNear3D(BlockPos blockPos, int distance) {
        final int x = blockPos.getX();
        final int y = blockPos.getY();
        final int z = blockPos.getZ();
        return streamNear1D(y, distance).boxed().flatMap(realY -> streamNear2D(new BlockPos(x, realY, z), distance));
    }

    // platform
    public static Stream<BlockPos> streamNear2D(BlockPos blockPos, int distance) {
        final int x = blockPos.getX();
        final int y = blockPos.getY();
        final int z = blockPos.getZ();
        return streamNear1D(x, distance).boxed().flatMap(realX -> streamNear1D(z, distance).mapToObj(realZ -> new BlockPos(realX, y, realZ)));
    }

    public static IntStream streamNear1D(int center, int distance) {
        return IntStream.rangeClosed(-distance, distance).map(index -> center + index);
    }
}
