package com.github.debris.debrisclient.mixin.compat.malilib;

import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WidgetListBase.class, remap = false)
public interface IMixinWidgetListBase {
    @Accessor
    void setAllowKeyboardNavigation(boolean allow);
}
