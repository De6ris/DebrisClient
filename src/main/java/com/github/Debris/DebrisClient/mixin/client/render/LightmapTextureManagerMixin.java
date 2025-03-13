package com.github.Debris.DebrisClient.mixin.client.render;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    @Inject(method = "getDarknessFactor", at = @At("HEAD"), cancellable = true)
    private void override(float delta, CallbackInfoReturnable<Float> cir) {
        if (DCCommonConfig.DarknessOverride.getBooleanValue()) {
            cir.setReturnValue(0.0F);
        }
    }
}
