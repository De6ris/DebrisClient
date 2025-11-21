package com.github.debris.debrisclient.mixin.client.gui;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface IMixinGuiContainer {
    @Accessor("hoveredSlot")
    Slot dc$getHoveredSlot();

    @Accessor("leftPos")
    int dc$getGuiLeft();
}
