package com.github.debris.debrisclient.mixin.compat.tweakeroo;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.AutoMoving;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.DummyMovementInput;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.player.ClientInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModReference.Tweakeroo))
@Mixin(value = DummyMovementInput.class, remap = false)
public abstract class DummyMovementInputMixin extends ClientInput {
    @Inject(method = "tick", at = @At("RETURN"), remap = true)
    private void autoMoving(CallbackInfo ci) {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && DCCommonConfig.FreeCamKeepAutoMoving.getBooleanValue() && AutoMoving.isAutoMoving()) {
            AutoMoving.handleMovement(this);
        } else {
            AutoMoving.clearMovement(this);
        }
    }
}
