package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.inventory.section.SectionHandler;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Shadow
    private static ItemGroup selectedTab;
    @Unique
    private ItemGroup cache = null;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "setSelectedTab", at = @At("HEAD"))
    private void onHead(ItemGroup group, CallbackInfo ci) {
        this.cache = selectedTab;
    }

    @Inject(method = "setSelectedTab", at = @At("RETURN"))
    private void onReturn(ItemGroup group, CallbackInfo ci) {
        if (this.cache.getType() == ItemGroup.Type.INVENTORY || group.getType() == ItemGroup.Type.INVENTORY) {
            SectionHandler.updateSection(this);
        }
    }
}
