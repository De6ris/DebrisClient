package com.github.Debris.DebrisClient.unsafe.worldEdit;

import com.mojang.logging.LogUtils;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

import java.util.Optional;

public class WorldEditRegionAccessor {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<Pair<BlockPos, BlockPos>> getRegion(String playerName) {
        LocalSession weSession = WorldEdit.getInstance().getSessionManager().findByName(playerName);
        if (weSession == null) return Optional.empty();
        com.sk89q.worldedit.world.World selectionWorld = weSession.getSelectionWorld();
        if (selectionWorld == null) return Optional.empty();
        RegionSelector regionSelector = weSession.getRegionSelector(selectionWorld);
        if (regionSelector instanceof CuboidRegionSelector cube && cube.isDefined()) {
            try {
                CuboidRegion region = cube.getRegion();
                BlockVector3 pos1 = region.getPos1();
                BlockVector3 pos2 = region.getPos2();
                BlockPos pos1Vanilla = new BlockPos(pos1.x(), pos1.y(), pos1.z());
                BlockPos pos2Vanilla = new BlockPos(pos2.x(), pos2.y(), pos2.z());
                return Optional.of(new Pair<>(pos1Vanilla, pos2Vanilla));
            } catch (IncompleteRegionException e) {
                LOGGER.error("why defined region still incomplete");
            }
        }
        return Optional.empty();
    }
}
