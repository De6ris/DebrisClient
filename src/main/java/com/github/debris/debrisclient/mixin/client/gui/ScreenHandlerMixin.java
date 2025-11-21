package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.inventory.autoprocess.AutoProcessManager;
import com.github.debris.debrisclient.inventory.section.IContainer;
import com.github.debris.debrisclient.inventory.section.SectionHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractContainerMenu.class)
public class ScreenHandlerMixin implements IContainer {
    @Shadow
    @Final
    private @Nullable MenuType<?> menuType;
    @Unique
    private SectionHandler sectionHandler;

    @Inject(method = "initializeContents", at = @At("RETURN"))
    private void onUpdate(int revision, List<ItemStack> stacks, ItemStack cursorStack, CallbackInfo ci) {
        AutoProcessManager.onContainerUpdate((AbstractContainerMenu) (Object) this);
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
        return this.menuType != null ? BuiltInRegistries.MENU.getKey(this.menuType).toString() : "<no type>";
    }
}
