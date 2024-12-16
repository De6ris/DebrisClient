package com.github.Debris.DebrisClient.mixin.gui;

import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffectsDisplay.class)
public abstract class AbstractInventoryScreenMixin {
    @Inject(method = "drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V", at = @At("HEAD"), cancellable = true)
    private void preventPotionEffectRendering(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        if (StoneCutterUtil.isStoneCutterRecipeViewOpen()) {
            ci.cancel();
        }
    }
}
