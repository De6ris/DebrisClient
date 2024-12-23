package com.github.Debris.DebrisClient.mixin.render;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.render.PathNodesRenderer;
import com.github.Debris.DebrisClient.render.RenderContext;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
    private <E extends Entity> boolean preventFrameRendering(boolean original, @Local(argsOnly = true) E entity) {
        EntityType<?> type = entity.getType();
        boolean isFrame = type == EntityType.GLOW_ITEM_FRAME || type == EntityType.ITEM_FRAME;
        if (original && DCCommonConfig.CancelFrameRendering.getBooleanValue() && isFrame) {
            return false;
        }
        return original;
    }

    @Inject(method =
            "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private <E extends Entity, S extends EntityRenderState> void onRenderPost(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer, CallbackInfo ci) {
        PathNodesRenderer.getInstance().onEntityRenderPost(entity, RenderContext.ofEntity(entity.getYaw(), tickDelta, matrices, vertexConsumers, light), tickDelta);
    }
}
