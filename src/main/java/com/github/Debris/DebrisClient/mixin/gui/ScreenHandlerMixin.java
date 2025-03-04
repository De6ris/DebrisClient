package com.github.Debris.DebrisClient.mixin.gui;

import com.github.Debris.DebrisClient.inventory.autoProcess.AutoProcessManager;
import com.github.Debris.DebrisClient.inventory.section.IContainer;
import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin implements IContainer {
    @Unique
    private SectionHandler sectionHandler;

    @Inject(method = "updateSlotStacks", at = @At("RETURN"))
    private void onUpdate(int revision, List<ItemStack> stacks, ItemStack cursorStack, CallbackInfo ci) {
        AutoProcessManager.onContainerUpdate((ScreenHandler) (Object) this);
    }

    @Override
    public void dc$setSectionHandler(SectionHandler sectionHandler) {
        this.sectionHandler = sectionHandler;
    }

    @Override
    public SectionHandler dc$getSectionHandler() {
        return this.sectionHandler;
    }
}
