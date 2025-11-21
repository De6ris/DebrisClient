package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.HeartType;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class InGameHudMixin {
    @WrapOperation(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui$HeartType;forPlayer(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/client/gui/Gui$HeartType;"))
    private Gui.HeartType overrideHeartType(Player player, Operation<Gui.HeartType> original) {
        HeartType heartType = DCCommonConfig.HeartTypeOverride.getEnumValue();
        if (heartType != HeartType.NONE) {
            return heartType.getVanilla();
        }
        return original.call(player);
    }
}
