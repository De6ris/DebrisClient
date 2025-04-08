package com.github.Debris.DebrisClient.mixin.client.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MineCrafterScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MineCrafterScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MineCrafterScreen.class)
public abstract class MineCrafterScreenMixin extends HandledScreen<MineCrafterScreenHandler> {
    public MineCrafterScreenMixin(MineCrafterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "donateExperience", at = @At("HEAD"))
    private void shiftDonate(ButtonWidget buttonWidget, CallbackInfo ci) {
        if (GuiBase.isShiftDown()) {
            for (int i = 0; i < 63; i++) {
                this.client.player.donateExperience();
            }
        }
    }
}
