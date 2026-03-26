package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.inventory.cutstone.StoneCutterRecipeRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "extractRenderState", at = @At(value = "RETURN"))
    private void onDrawScreenPost(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        StoneCutterRecipeRenderer.getInstance().renderStoneCutterRecipe(graphics, mouseX, mouseY);
    }
}
