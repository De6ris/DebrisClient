package com.github.debris.debrisclient.mixin.compat.malilib;

import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = WidgetListBase.class, remap = false)
public interface IMixinWidgetListBase<TYPE, WIDGET extends WidgetListEntryBase<TYPE>> {
    @Accessor
    void setAllowKeyboardNavigation(boolean allow);

    @Accessor
    List<WIDGET> getListWidgets();
}
