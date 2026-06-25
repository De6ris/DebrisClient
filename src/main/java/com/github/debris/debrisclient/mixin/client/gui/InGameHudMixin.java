package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.HeartType;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Hud.class)
public class InGameHudMixin {
    @WrapOperation(
            method = "extractHearts",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Hud$HeartType;forPlayer(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/client/gui/Hud$HeartType;"
            )
    )
    private Hud.HeartType overrideHeartType(Player player, Operation<Hud.HeartType> original) {
        HeartType heartType = DCCommonConfig.HeartTypeOverride.getEnumValue();
        if (heartType != HeartType.NONE) {
            return heartType.getVanilla();
        }
        return original.call(player);
    }
}
