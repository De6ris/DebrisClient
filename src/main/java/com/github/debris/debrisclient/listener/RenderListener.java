package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.render.PathNodesRenderer;
import com.github.debris.debrisclient.render.RenderContext;
import com.github.debris.debrisclient.render.RenderQueue;
import com.github.debris.debrisclient.render.WorldRenderContext;
import com.github.debris.debrisclient.unsafe.LitematicaAccess;
import com.github.debris.debrisclient.unsafe.MagicLibAccess;
import com.github.debris.debrisclient.unsafe.MiniHudAccess;
import com.github.debris.debrisclient.unsafe.WorldEditAccess;
import com.github.debris.debrisclient.util.Predicates;
import com.github.debris.debrisclient.util.RayTraceUtil;
import com.mojang.blaze3d.pipeline.RenderTarget;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.joml.Matrix4f;

public class RenderListener implements IRenderer {
    private final static RenderListener Instance = new RenderListener();

    public static RenderListener getInstance() {
        return Instance;
    }

    private final Minecraft client = Minecraft.getInstance();

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onRenderWorldLastAdvanced(RenderTarget fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, RenderBuffers buffers, ProfilerFiller profiler) {
        if (Predicates.notInGame(this.client)) return;

        if (DCCommonConfig.WorldEditVisibility.getBooleanValue() && ModReference.hasMod(ModReference.WorldEdit) && ModReference.hasMod(ModReference.Litematica)) {
            WorldEditAccess.getRegion(this.client.player.getScoreboardName())
                    .ifPresent(x -> LitematicaAccess.renderWorldEditSelectionBox(x.getA(), x.getB(), posMatrix));
        }

        if (DCCommonConfig.InventoryPreviewSupportComparator.getBooleanValue() && ModReference.hasMod(ModReference.MiniHud) && MiniHudAccess.isPreviewingInventory() && ModReference.hasMod(ModReference.MagicLibMCApi)) {
            RayTraceUtil.getRayTraceBlock(this.client).ifPresent(pos -> {
                Level world = WorldUtils.getBestWorld(this.client);// get it through chunk, since the server return you null if you call world.getBlockEntity directly on render thread
                world.getChunkAt(pos).getBlockEntity(pos, BlockEntityType.COMPARATOR).ifPresent(comparator -> MagicLibAccess.renderText(comparator.getOutputSignal(), pos));
            });
        }

        float tickDelta = this.client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        WorldRenderContext context = RenderContext.ofWorld(fb, posMatrix, projMatrix, frustum, camera, buffers, profiler, tickDelta);

        PathNodesRenderer.getInstance().onRenderWorldPost(this.client.level, context);

        RenderQueue.onRenderWorldPost(context);
    }
}
