package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.inventory.autoprocess.AutoProcessManager;
import com.github.debris.debrisclient.inventory.section.IContainer;
import com.github.debris.debrisclient.inventory.section.SectionHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin implements IContainer {
    @Shadow
    @Final
    private @Nullable ScreenHandlerType<?> type;
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

    @SuppressWarnings("DataFlowIssue")
    @Override
    public String dc$getTypeString() {
        return this.type != null ? Registries.SCREEN_HANDLER.getId(this.type).toString() : "<no type>";
    }
}
