package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.blockentity.TheEndGatewayRenderer;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TheEndGatewayRenderer.class)
public class EndGatewayBlockEntityRendererMixin {
    @WrapOperation(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/EndGatewayRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;isSpawning()Z"))
    private boolean forceRender(TheEndGatewayBlockEntity instance, Operation<Boolean> original) {
        if (DCCommonConfig.ForceRenderEndGatewayBeam.getBooleanValue()) return true;
        return original.call(instance);
    }

    @WrapOperation(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/EndGatewayRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;getSpawnPercent(F)F"))
    private float heightOverride(TheEndGatewayBlockEntity instance, float tickDelta, Operation<Float> original) {
        if (DCCommonConfig.ForceRenderEndGatewayBeam.getBooleanValue()) return 0.5F;
        return original.call(instance, tickDelta);
    }
}

