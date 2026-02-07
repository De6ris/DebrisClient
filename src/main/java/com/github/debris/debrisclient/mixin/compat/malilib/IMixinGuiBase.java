package com.github.debris.debrisclient.mixin.compat.malilib;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = GuiBase.class, remap = false)
public interface IMixinGuiBase {
    @Accessor
    List<ButtonBase> getButtons();

    @Accessor
    WidgetBase getHoveredWidget();

    @Accessor
    List<WidgetBase> getWidgets();
}
