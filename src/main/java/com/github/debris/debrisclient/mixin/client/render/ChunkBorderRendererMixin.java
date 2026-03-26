package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.debug.ChunkBorderRenderer;
import net.minecraft.gizmos.GizmoProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkBorderRenderer.class)
public class ChunkBorderRendererMixin {
    @WrapOperation(method = "emitGizmos", at = @At(value = "INVOKE", target = "Lnet/minecraft/gizmos/GizmoProperties;setAlwaysOnTop()Lnet/minecraft/gizmos/GizmoProperties;"))
    private GizmoProperties notOnTop(GizmoProperties instance, Operation<GizmoProperties> original) {
        if (DCCommonConfig.ChunkBorderRenderNotOnTop.getBooleanValue()) return instance;
        return original.call(instance);
    }
}
