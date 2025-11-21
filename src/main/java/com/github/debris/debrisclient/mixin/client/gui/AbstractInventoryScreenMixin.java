package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.inventory.cutstone.StoneCutterUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectsInInventory.class)
public abstract class AbstractInventoryScreenMixin {
    @Inject(method = "renderEffects(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("HEAD"), cancellable = true)
    private void preventPotionEffectRendering(GuiGraphics context, int mouseX, int mouseY, CallbackInfo ci) {
        if (StoneCutterUtil.isStoneCutterRecipeViewOpen()) {
            ci.cancel();
        }
    }
}
