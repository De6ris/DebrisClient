package com.github.debris.debrisclient.mixin.world.entity;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "isReducedDebugInfo", at = @At("HEAD"), cancellable = true)
    public void override(CallbackInfoReturnable<Boolean> cir) {
        if (DCCommonConfig.FullDebugInfo.getBooleanValue()) cir.setReturnValue(false);
    }
}
