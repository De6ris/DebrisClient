package com.github.debris.debrisclient.mixin.client.gui;

import com.github.debris.debrisclient.inventory.autoprocess.IAutoProcessScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements IAutoProcessScreen {
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
