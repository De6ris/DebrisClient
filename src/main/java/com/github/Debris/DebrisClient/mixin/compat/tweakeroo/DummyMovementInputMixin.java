package com.github.Debris.DebrisClient.mixin.compat.tweakeroo;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.feat.AutoMoving;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.DummyMovementInput;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModReference.Tweakeroo))
@Mixin(value = DummyMovementInput.class, remap = false)
public abstract class DummyMovementInputMixin extends Input {
    @Inject(method = "tick", at = @At("RETURN"), remap = true)
    private void autoMoving(CallbackInfo ci) {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && DCCommonConfig.FreeCamKeepAutoMoving.getBooleanValue() && AutoMoving.isAutoMoving()) {
            AutoMoving.handleMovement(this);
        } else {
            AutoMoving.clearMovement(this);
        }
    }
}
