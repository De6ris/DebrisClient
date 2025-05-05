package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    @Inject(method = "getDarkness", at = @At("HEAD"), cancellable = true)
    private void override(LivingEntity entity, float factor, float tickProgress, CallbackInfoReturnable<Float> cir) {
        if (DCCommonConfig.DarknessOverride.getBooleanValue()) {
            cir.setReturnValue(0.0F);
        }
    }
}
