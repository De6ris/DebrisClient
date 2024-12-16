package com.github.Debris.DebrisClient.mixin.gui;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface IMixinGuiContainer {
    @Accessor("focusedSlot")
    Slot dc$getHoveredSlot();

    @Accessor("x")
    int dc$getGuiLeft();
}
