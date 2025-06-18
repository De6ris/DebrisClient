package com.github.debris.debrisclient.mixin.client.sound;

import com.github.debris.debrisclient.util.CullingUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @ModifyExpressionValue(
            method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/sound/SoundInstance;canPlay()Z"
            )
    )
    private boolean cullSoundOnAdd(boolean original, @Local(argsOnly = true) SoundInstance soundInstance) {
        return original && !CullingUtil.shouldMuteSound(soundInstance);
    }

    @WrapOperation(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/sound/TickableSoundInstance;canPlay()Z"
            )
    )
    private boolean cullSoundOnTick(TickableSoundInstance instance, Operation<Boolean> original) {
        return original.call(instance) && !CullingUtil.shouldMuteSound(instance);
    }
}
