package com.github.debris.debrisclient.mixin.client;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(DefaultPlayerSkin.class)
public class DefaultSkinHelperMixin {
    @Shadow
    @Final
    private static PlayerSkin[] DEFAULT_SKINS;

    @Inject(method = "get(Ljava/util/UUID;)Lnet/minecraft/world/entity/player/PlayerSkin;", at = @At("HEAD"), cancellable = true)
    private static void onlyOldSkins(UUID uuid, CallbackInfoReturnable<PlayerSkin> cir) {
        if (DCCommonConfig.RetroDefaultSkin.getBooleanValue()) {
            if ((uuid.hashCode() & 1) == 1) {
                cir.setReturnValue(DEFAULT_SKINS[0]);
            } else {
                cir.setReturnValue(DEFAULT_SKINS[15]);
            }
        }
    }
}
