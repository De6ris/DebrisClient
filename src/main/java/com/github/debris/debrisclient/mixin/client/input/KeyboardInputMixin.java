package com.github.debris.debrisclient.mixin.client.input;

import com.github.debris.debrisclient.feat.AutoMoving;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {
    @Inject(method = "tick", at = @At("RETURN"))
    private void autoMove(CallbackInfo ci) {
        AutoMoving.tickInput(this);
    }
}
