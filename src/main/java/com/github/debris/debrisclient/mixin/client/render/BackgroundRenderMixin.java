package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class BackgroundRenderMixin {
    @Inject(method = "getFogModifier", at = @At("HEAD"), cancellable = true)
    private static void override(Entity entity, float tickDelta, CallbackInfoReturnable<BackgroundRenderer.StatusEffectFogModifier> cir) {
        if (DCCommonConfig.DarknessOverride.getBooleanValue()) {
            cir.setReturnValue(null);
        }
    }
}
