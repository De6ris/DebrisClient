package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.inventory.section.SectionHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow
    private static CreativeModeTab selectedTab;
    @Unique
    private CreativeModeTab cache = null;

    public CreativeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "selectTab", at = @At("HEAD"))
    private void onHead(CreativeModeTab group, CallbackInfo ci) {
        this.cache = selectedTab;
    }

    @Inject(method = "selectTab", at = @At("RETURN"))
    private void onReturn(CreativeModeTab group, CallbackInfo ci) {
        if (this.cache.getType() == CreativeModeTab.Type.INVENTORY || group.getType() == CreativeModeTab.Type.INVENTORY) {
            SectionHandler.updateSection(this);
        }
    }
}
