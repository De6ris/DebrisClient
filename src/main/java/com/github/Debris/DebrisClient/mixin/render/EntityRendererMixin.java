package com.github.Debris.DebrisClient.mixin.render;

import com.github.Debris.DebrisClient.render.PathNodesRenderer;
import com.github.Debris.DebrisClient.render.RenderContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderPost(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        PathNodesRenderer.getInstance().onEntityRenderPost(entity, RenderContext.ofEntity(yaw, tickDelta, matrices, vertexConsumers, light), tickDelta);
    }
}
