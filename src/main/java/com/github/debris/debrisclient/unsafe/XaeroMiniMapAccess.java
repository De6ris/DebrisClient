package com.github.debris.debrisclient.unsafe;

import net.minecraft.core.BlockPos;
import xaero.common.HudMod;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;

public class XaeroMiniMapAccess {
    public static void addDestination(BlockPos blockPos, String name) {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();

        MinimapWorldManager waypointsManager = session.getWorldManager();
        MinimapWorld currentWorld = waypointsManager.getCurrentWorld();
        if (currentWorld == null) return;

        Waypoint waypoint = new Waypoint(
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ(),
                name,
                name.isEmpty() ? "X" : name.substring(0, 1),
                WaypointColor.getRandom(),
                WaypointPurpose.DESTINATION,
                true
        );

        currentWorld.getCurrentWaypointSet().add(waypoint, !HudMod.INSTANCE.getSettings().waypointsBottom);
    }
}
