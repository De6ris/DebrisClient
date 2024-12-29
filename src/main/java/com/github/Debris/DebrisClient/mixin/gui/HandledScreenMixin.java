package com.github.Debris.DebrisClient.mixin.gui;

import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import com.github.Debris.DebrisClient.inventory.autoProcess.IAutoProcessScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements IAutoProcessScreen {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        SectionHandler.updateSection((HandledScreen<?>) (Object) this);
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void onClose(CallbackInfo ci) {
        SectionHandler.clear();
    }

    @Unique
    private boolean processFlag;

    @Override
    public void dc$setShouldProcess(boolean flag) {
        this.processFlag = flag;
    }

    @Override
    public boolean dc$shouldProcess() {
        return this.processFlag;
    }
}
