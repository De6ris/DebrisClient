package com.github.Debris.DebrisClient.mixin.client.gui;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @WrapOperation(method = "renderHealthBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud$HeartType;fromPlayerState(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/client/gui/hud/InGameHud$HeartType;"))
    private InGameHud.HeartType overrideHeartType(PlayerEntity player, Operation<InGameHud.HeartType> original) {
        if (DCCommonConfig.HeartTypeOverride.getBooleanValue()) {
            return DCCommonConfig.HeartTypeValue.getEnumValue().getVanilla();
        }
        return original.call(player);
    }
}
