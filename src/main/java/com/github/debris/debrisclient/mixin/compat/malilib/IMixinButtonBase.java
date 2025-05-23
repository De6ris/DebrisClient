package com.github.debris.debrisclient.mixin.compat.malilib;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ButtonBase.class, remap = false)
public interface IMixinButtonBase {
    @Accessor
    IButtonActionListener getActionListener();

    @Accessor
    String getDisplayString();
}
