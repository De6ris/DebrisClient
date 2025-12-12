package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.renderer.debug.ChunkBorderRenderer;
import net.minecraft.gizmos.GizmoProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkBorderRenderer.class)
public class ChunkBorderRendererMixin {
    @SuppressWarnings("RedundantIfStatement")
    @WrapWithCondition(method = "emitGizmos", at = @At(value = "INVOKE", target = "Lnet/minecraft/gizmos/GizmoProperties;setAlwaysOnTop()Lnet/minecraft/gizmos/GizmoProperties;"))
    private boolean notOnTop(GizmoProperties instance) {
        if (DCCommonConfig.ChunkBorderRenderNotOnTop.getBooleanValue()) return false;
        return true;
    }
}
