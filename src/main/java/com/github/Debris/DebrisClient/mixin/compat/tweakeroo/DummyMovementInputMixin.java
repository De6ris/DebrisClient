package com.github.Debris.DebrisClient.mixin.compat.tweakeroo;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.MiscUtil;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.DummyMovementInput;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DummyMovementInput.class, remap = false)
public abstract class DummyMovementInputMixin extends Input {
    @Inject(method = "tick", at = @At("RETURN"), remap = true)
    private void autoMoving(CallbackInfo ci) {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && DCCommonConfig.FreeCamKeepAutoMoving.getBooleanValue() && MiscUtil.isAutoMoving()) {
            MiscUtil.handleMovement(this);
        } else {
            MiscUtil.clearMovement(this);
        }
    }
}
