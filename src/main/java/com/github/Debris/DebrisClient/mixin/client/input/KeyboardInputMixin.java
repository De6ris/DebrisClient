package com.github.Debris.DebrisClient.mixin.client.input;

import com.github.Debris.DebrisClient.util.MiscUtil;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
    @Inject(method = "tick", at = @At("RETURN"))
    private void autoWalk(CallbackInfo ci) {
        MiscUtil.handleMovement(this);
    }
}
