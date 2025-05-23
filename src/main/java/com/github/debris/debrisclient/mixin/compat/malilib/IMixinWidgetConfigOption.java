package com.github.debris.debrisclient.mixin.compat.malilib;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WidgetConfigOption.class, remap = false)
public interface IMixinWidgetConfigOption {
    @Accessor
    GuiConfigsBase.ConfigOptionWrapper getWrapper();
}
