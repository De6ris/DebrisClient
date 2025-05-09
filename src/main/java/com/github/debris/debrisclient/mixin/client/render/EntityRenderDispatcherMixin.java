package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.render.PathNodesRenderer;
import com.github.debris.debrisclient.render.RenderContext;
import com.github.debris.debrisclient.util.CullingUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
    private <E extends Entity> boolean cullEntity(boolean original, @Local(argsOnly = true) E entity) {
        return original && !CullingUtil.shouldCullEntity(entity.getType());
    }

    @Inject(method =
            "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V"))
    private <E extends Entity, S extends EntityRenderState> void onRenderPost(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer, CallbackInfo ci) {
        PathNodesRenderer.getInstance().onEntityRenderPost(entity, RenderContext.ofEntity(entity.getYaw(), tickDelta, matrices, vertexConsumers, light));
    }
}
