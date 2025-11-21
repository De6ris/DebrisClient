package com.github.debris.debrisclient.mixin.client.sound;

import com.github.debris.debrisclient.util.CullingUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundEngine.class)
public class SoundSystemMixin {
    @ModifyExpressionValue(
            method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/sounds/SoundInstance;canPlaySound()Z"
            )
    )
    private boolean cullSoundOnAdd(boolean original, @Local(argsOnly = true) SoundInstance soundInstance) {
        return original && !CullingUtil.shouldMuteSound(soundInstance);
    }

    @WrapOperation(
            method = "tickInGameSound()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/sounds/TickableSoundInstance;canPlaySound()Z"
            )
    )
    private boolean cullSoundOnTick(TickableSoundInstance instance, Operation<Boolean> original) {
        return original.call(instance) && !CullingUtil.shouldMuteSound(instance);
    }
}
