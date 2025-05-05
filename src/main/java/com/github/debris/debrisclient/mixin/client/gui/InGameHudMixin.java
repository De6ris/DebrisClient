package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.HeartType;
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
        HeartType heartType = DCCommonConfig.HeartTypeOverride.getEnumValue();
        if (heartType != HeartType.NONE) {
            return heartType.getVanilla();
        }
        return original.call(player);
    }
}
