package com.github.debris.debrisclient.event;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.render.RenderContext;
import com.github.debris.debrisclient.render.RenderQueue;
import com.github.debris.debrisclient.render.RendererFactory;
import com.github.debris.debrisclient.render.WorldRenderContext;
import com.github.debris.debrisclient.unsafe.LitematicaAccess;
import com.github.debris.debrisclient.unsafe.MiniHudAccess;
import com.github.debris.debrisclient.unsafe.WorldEditAccess;
import com.github.debris.debrisclient.util.Predicates;
import com.github.debris.debrisclient.util.RayTraceUtil;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.joml.Vector4f;

public class RenderListener implements IRenderer {
    private final static RenderListener Instance = new RenderListener();

    public static RenderListener getInstance() {
        return Instance;
    }

    private final Minecraft client = Minecraft.getInstance();

    @Override
    public void onRenderWorldLast(RenderTarget fb, CameraRenderState cameraState, Frustum culling, RenderBuffers buffers, GpuBufferSlice terrainFog, Vector4f fogColor, ProfilerFiller profiler) {
        if (Predicates.notInGame(this.client)) return;

        if (DCCommonConfig.WorldEditVisibility.getBooleanValue() && ModReference.hasMod(ModReference.WorldEdit) && ModReference.hasMod(ModReference.Litematica)) {
            WorldEditAccess.getRegion(this.client.player.getScoreboardName())
                    .ifPresent(x -> LitematicaAccess.renderWorldEditSelectionBox(x.getFirst(), x.getSecond()));
        }

        if (DCCommonConfig.InventoryPreviewSupportComparator.getBooleanValue() && ModReference.hasMod(ModReference.MiniHud) && MiniHudAccess.isPreviewingInventory()) {
            RayTraceUtil.getRayTraceBlock(this.client).ifPresent(pos -> {
                Level world = WorldUtils.getBestWorld(this.client);// get it through chunk, since the server return you null if you call world.getBlockEntity directly on render thread
                world.getChunkAt(pos)
                        .getBlockEntity(pos, BlockEntityTypes.COMPARATOR)
                        .ifPresent(comparator ->
                                RendererFactory.text(Component.literal(String.valueOf(comparator.getOutputSignal())), pos)
                        );
            });
        }

        float tickDelta = this.client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        WorldRenderContext context = RenderContext.ofWorld(fb, cameraState, culling, buffers, profiler, tickDelta);

        RenderQueue.onRenderWorldPost(context);
    }
}
