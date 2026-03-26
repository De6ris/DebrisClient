package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.render.PathNodesRenderer;
import com.github.debris.debrisclient.render.RenderContext;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "extractVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;extractEntity(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"))
    private void onRenderStateUpdate(Camera camera, Frustum frustum, DeltaTracker deltaTracker, net.minecraft.client.renderer.state.level.LevelRenderState output, CallbackInfo ci, @Local Entity entity, @Local float tickDelta) {
        PathNodesRenderer.getInstance().onEntityRenderPost(entity, RenderContext.ofEntity(entity.getYRot(), tickDelta));
    }
}
