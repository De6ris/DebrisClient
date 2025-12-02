package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.DarknessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @WrapOperation(
            method = {"computeFogColor", "setupFog"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;isApplicable(Lnet/minecraft/world/level/material/FogType;Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean darknessOverride(FogEnvironment instance, FogType cameraSubmersionType, Entity entity, Operation<Boolean> original) {
        if ((instance instanceof DarknessFogEnvironment) && DCCommonConfig.DarknessOverride.getBooleanValue())
            return false;
        return original.call(instance, cameraSubmersionType, entity);
    }
}
