package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.render.PathNodesRenderer;
import com.github.debris.debrisclient.render.RenderContext;
import com.github.debris.debrisclient.render.RenderQueue;
import com.github.debris.debrisclient.render.WorldRenderContext;
import com.github.debris.debrisclient.unsafe.litematica.LitematicaAccessor;
import com.github.debris.debrisclient.unsafe.magicLib.MagicLibAccessor;
import com.github.debris.debrisclient.unsafe.miniHud.MiniHudAccessor;
import com.github.debris.debrisclient.unsafe.worldEdit.WorldEditRegionAccessor;
import com.github.debris.debrisclient.util.Predicates;
import com.github.debris.debrisclient.util.RayTraceUtil;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.joml.Matrix4f;

public class RenderListener implements IRenderer {
    private final static RenderListener Instance = new RenderListener();

    public static RenderListener getInstance() {
        return Instance;
    }

    private final MinecraftClient client = MinecraftClient.getInstance();

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onRenderWorldLastAdvanced(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, BufferBuilderStorage buffers, Profiler profiler) {
        if (Predicates.notInGame(this.client)) return;

        if (DCCommonConfig.WorldEditVisibility.getBooleanValue() && ModReference.hasMod(ModReference.WorldEdit) && ModReference.hasMod(ModReference.Litematica)) {
            WorldEditRegionAccessor.getRegion(this.client.player.getNameForScoreboard())
                    .ifPresent(x -> LitematicaAccessor.renderWorldEditSelectionBox(x.getLeft(), x.getRight(), posMatrix));
        }

        if (DCCommonConfig.InventoryPreviewSupportComparator.getBooleanValue() && ModReference.hasMod(ModReference.MiniHud) && MiniHudAccessor.isPreviewingInventory() && ModReference.hasMod(ModReference.MagicLibMCApi)) {
            RayTraceUtil.getRayTraceBlock(this.client).ifPresent(pos -> {
                World world = WorldUtils.getBestWorld(this.client);// get it through chunk, since the server return you null if you call world.getBlockEntity directly on render thread
                world.getWorldChunk(pos).getBlockEntity(pos, BlockEntityType.COMPARATOR).ifPresent(comparator -> MagicLibAccessor.renderText(comparator.getOutputSignal(), pos));
            });
        }

        float tickDelta = this.client.getRenderTickCounter().getTickProgress(false);
        WorldRenderContext context = RenderContext.ofWorld(fb, posMatrix, projMatrix, frustum, camera, buffers, profiler, tickDelta);

        PathNodesRenderer.getInstance().onRenderWorldPost(this.client.world, context);

        RenderQueue.onRenderWorldPost(context);
    }
}
