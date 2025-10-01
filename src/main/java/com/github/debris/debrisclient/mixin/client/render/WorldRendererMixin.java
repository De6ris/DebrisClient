package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.render.PathNodesRenderer;
import com.github.debris.debrisclient.render.RenderContext;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "hasBlindnessOrDarkness", at = @At("HEAD"), cancellable = true)
    private void override(Camera camera, CallbackInfoReturnable<Boolean> cir) {
        if (DCCommonConfig.DarknessOverride.getBooleanValue()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "fillEntityRenderStates", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;getAndUpdateRenderState(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/entity/state/EntityRenderState;"))
    private void onRenderStateUpdate(Camera camera, Frustum frustum, RenderTickCounter tickCounter, WorldRenderState renderStates, CallbackInfo ci, @Local Entity entity, @Local float tickDelta) {
        PathNodesRenderer.getInstance().onEntityRenderPost(entity, RenderContext.ofEntity(entity.getYaw(), tickDelta));
    }
}
