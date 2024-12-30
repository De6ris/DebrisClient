package com.github.Debris.DebrisClient.mixin.misc;

import com.github.Debris.DebrisClient.util.CullingUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @ModifyExpressionValue(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundInstance;canPlay()Z"))
    private boolean modifySound(boolean original, @Local(argsOnly = true) SoundInstance soundInstance) {
        if (CullingUtil.shouldMuteSound(soundInstance)) return false;
        return original;
    }
}
