package com.github.debris.debrisclient.render;

import com.github.debris.debrisclient.feat.log.GameLogs;
import com.github.debris.debrisclient.util.RenderUtil;
import com.github.debris.debrisclient.util.SyncUtil;
import com.google.common.collect.Queues;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Queue;

public class PathNodesRenderer {
    private static final PathNodesRenderer Instance = new PathNodesRenderer();

    public static PathNodesRenderer getInstance() {
        return Instance;
    }

    private final Queue<MobEntity> mobQueue = Queues.newConcurrentLinkedQueue();

    @SuppressWarnings("ConstantConditions")
    public void onEntityRenderPost(Entity entity, EntityRenderContext context) {
        if (GameLogs.PATH_NODE.isInactive()) return;

        Vec3d camPos = EntityUtils.getCameraEntity().getPos();
        if (entity.getPos().distanceTo(camPos) > 64) return;// cull those far away

        if (GameLogs.PATH_NODE.onlyNamed() && !entity.hasCustomName()) return;

        if (entity instanceof MobEntity mob) {
            this.mobQueue.add(mob);
        }
    }

    public void onRenderWorldPost(World world, WorldRenderContext context) {
        if (GameLogs.PATH_NODE.isInactive()) return;

        float partialTicks = context.getTickDelta();

        for (MobEntity clientEntity : this.mobQueue) {
            MobEntity serverEntity = (MobEntity) SyncUtil.syncEntityDataFromIntegratedServer(clientEntity);
            Path currentPath = serverEntity.getNavigation().getCurrentPath();
            if (currentPath != null) {
                for (int i = 0; i < currentPath.getLength() - 1; i++) {
                    BlockPos current = currentPath.getNodePos(i);
                    BlockPos next = currentPath.getNodePos(i + 1);

                    Vec3d start = new Vec3d(current.getX() + 0.5, current.getY() + 0.5, current.getZ() + 0.5);
                    Vec3d end = new Vec3d(next.getX() + 0.5, next.getY() + 0.5, next.getZ() + 0.5);
                    RenderUtil.drawConnectLine(start, end, 0.05, new Color4f(1, 1, 1), new Color4f(0, 0, 1), new Color4f(0, 0, 1));
                }
            }

            MoveControl moveControl = serverEntity.getMoveControl();
            if (moveControl.getTargetX() != 0 && moveControl.getTargetY() != 0 && moveControl.getTargetZ() != 0) {
                Vec3d start = clientEntity.getCameraPosVec(partialTicks);
                Vec3d end = new Vec3d(moveControl.getTargetX(), moveControl.getTargetY(), moveControl.getTargetZ());
                RenderUtil.drawConnectLine(start, end, 0.05, new Color4f(1, 1, 1), new Color4f(0, 0, 1), new Color4f(0, 0, 1));
            }
        }

        this.mobQueue.clear();
    }
}
