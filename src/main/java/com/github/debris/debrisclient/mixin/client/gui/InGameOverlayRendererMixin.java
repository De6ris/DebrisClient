package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void cull(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (DCCommonConfig.CullFireAnimation.getBooleanValue()) ci.cancel();
    }
}
