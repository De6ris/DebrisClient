package com.github.debris.debrisclient.mixin.entity;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "hasReducedDebugInfo", at = @At("HEAD"), cancellable = true)
    public void override(CallbackInfoReturnable<Boolean> cir) {
        if (DCCommonConfig.FullDebugInfo.getBooleanValue()) cir.setReturnValue(false);
    }
}
