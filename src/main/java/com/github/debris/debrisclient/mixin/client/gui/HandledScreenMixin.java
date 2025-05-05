package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.inventory.autoprocess.IAutoProcessScreen;
import com.github.debris.debrisclient.inventory.section.SectionHandler;
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
