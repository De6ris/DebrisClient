package com.github.Debris.DebrisClient.mixin.client.gui;

import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterRecipeRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "renderWithTooltip", at = @At(value = "RETURN"))
    private void onDrawScreenPost(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        StoneCutterRecipeRenderer.getInstance().renderStoneCutterRecipe(context, mouseX, mouseY);
    }
}
