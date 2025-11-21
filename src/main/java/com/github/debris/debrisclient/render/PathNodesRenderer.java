package com.github.debris.debrisclient.render;

import com.github.debris.debrisclient.feat.log.GameLogs;
import com.github.debris.debrisclient.util.RenderUtil;
import com.github.debris.debrisclient.util.SyncUtil;
import com.google.common.collect.Queues;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.Queue;

public class PathNodesRenderer {
    private static final PathNodesRenderer Instance = new PathNodesRenderer();

    public static PathNodesRenderer getInstance() {
        return Instance;
    }

    private final Queue<Mob> mobQueue = Queues.newConcurrentLinkedQueue();

    @SuppressWarnings("ConstantConditions")
    public void onEntityRenderPost(Entity entity, EntityRenderContext context) {
        if (GameLogs.PATH_NODE.isInactive()) return;

        Vec3 camPos = EntityUtils.getCameraEntity().position();
        if (entity.position().distanceTo(camPos) > 64) return;// cull those far away

        if (GameLogs.PATH_NODE.onlyNamed() && !entity.hasCustomName()) return;

        if (entity instanceof Mob mob) {
            this.mobQueue.add(mob);
        }
    }

    public void onRenderWorldPost(Level world, WorldRenderContext context) {
        if (GameLogs.PATH_NODE.isInactive()) return;

        float partialTicks = context.getTickDelta();

        for (Mob clientEntity : this.mobQueue) {
            Mob serverEntity = (Mob) SyncUtil.syncEntityDataFromIntegratedServer(clientEntity);
            Path currentPath = serverEntity.getNavigation().getPath();
            if (currentPath != null) {
                for (int i = 0; i < currentPath.getNodeCount() - 1; i++) {
                    BlockPos current = currentPath.getNodePos(i);
                    BlockPos next = currentPath.getNodePos(i + 1);

                    Vec3 start = new Vec3(current.getX() + 0.5, current.getY() + 0.5, current.getZ() + 0.5);
                    Vec3 end = new Vec3(next.getX() + 0.5, next.getY() + 0.5, next.getZ() + 0.5);
                    RenderUtil.drawConnectLine(start, end, 0.05, new Color4f(1, 1, 1), new Color4f(0, 0, 1), new Color4f(0, 0, 1));
                }
            }

            MoveControl moveControl = serverEntity.getMoveControl();
            if (moveControl.getWantedX() != 0 && moveControl.getWantedY() != 0 && moveControl.getWantedZ() != 0) {
                Vec3 start = clientEntity.getEyePosition(partialTicks);
                Vec3 end = new Vec3(moveControl.getWantedX(), moveControl.getWantedY(), moveControl.getWantedZ());
                RenderUtil.drawConnectLine(start, end, 0.05, new Color4f(1, 1, 1), new Color4f(0, 0, 1), new Color4f(0, 0, 1));
            }
        }

        this.mobQueue.clear();
    }
}
