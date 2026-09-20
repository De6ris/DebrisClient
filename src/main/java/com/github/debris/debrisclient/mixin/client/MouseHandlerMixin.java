package com.github.debris.debrisclient.mixin.client;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @ModifyArg(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"), index = 0)
    private double autoRotate(double xo) {
        if (DCCommonConfig.AUTO_ROTATE.getBooleanValue() && xo == 0) return 120;
        return xo;
    }
}
