package com.github.debris.debrisclient.unsafe;

import com.mojang.logging.LogUtils;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import org.slf4j.Logger;

import java.util.Optional;

public class WorldEditAccess {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<Tuple<BlockPos, BlockPos>> getRegion(String playerName) {
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
                return Optional.of(new Tuple<>(pos1Vanilla, pos2Vanilla));
            } catch (IncompleteRegionException e) {
                LOGGER.error("why defined region still incomplete");
            }
        }
        return Optional.empty();
    }
}
