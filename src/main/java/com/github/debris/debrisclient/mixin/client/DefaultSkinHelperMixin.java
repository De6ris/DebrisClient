package com.github.debris.debrisclient.mixin.client;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(DefaultSkinHelper.class)
public class DefaultSkinHelperMixin {
    @Shadow
    @Final
    private static SkinTextures[] SKINS;

    @Inject(method = "getSkinTextures(Ljava/util/UUID;)Lnet/minecraft/entity/player/SkinTextures;", at = @At("HEAD"), cancellable = true)
    private static void onlyOldSkins(UUID uuid, CallbackInfoReturnable<SkinTextures> cir) {
        if (DCCommonConfig.RetroDefaultSkin.getBooleanValue()) {
            if ((uuid.hashCode() & 1) == 1) {
                cir.setReturnValue(SKINS[0]);
            } else {
                cir.setReturnValue(SKINS[15]);
            }
        }
    }
}
