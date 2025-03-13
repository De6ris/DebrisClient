package com.github.Debris.DebrisClient.mixin.client.render;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.render.block.entity.EndGatewayBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndGatewayBlockEntityRenderer.class)
public class EndGatewayBlockEntityRendererMixin {
    @WrapOperation(method = "render(Lnet/minecraft/block/entity/EndGatewayBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/EndGatewayBlockEntity;isRecentlyGenerated()Z"))
    private boolean forceRender(EndGatewayBlockEntity instance, Operation<Boolean> original) {
        if (DCCommonConfig.ForceRenderEndGatewayBeam.getBooleanValue()) return true;
        return original.call(instance);
    }

    @WrapOperation(method = "render(Lnet/minecraft/block/entity/EndGatewayBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/EndGatewayBlockEntity;getRecentlyGeneratedBeamHeight(F)F"))
    private float heightOverride(EndGatewayBlockEntity instance, float tickDelta, Operation<Float> original) {
        if (DCCommonConfig.ForceRenderEndGatewayBeam.getBooleanValue()) return 0.5F;
        return original.call(instance, tickDelta);
    }
}

