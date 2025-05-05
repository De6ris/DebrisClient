package com.github.debris.debrisclient.mixin.client.sound;

import com.github.debris.debrisclient.util.CullingUtil;
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
        return original && !CullingUtil.shouldMuteSound(soundInstance);
    }

    @ModifyExpressionValue(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getSoundVolume(Lnet/minecraft/sound/SoundCategory;)F", ordinal = 0))
    private float muteSound(float original, @Local SoundInstance soundInstance) {
        if (original <= 0.0F || CullingUtil.shouldMuteSound(soundInstance)) return 0.0F;
        return original;
    }
}
