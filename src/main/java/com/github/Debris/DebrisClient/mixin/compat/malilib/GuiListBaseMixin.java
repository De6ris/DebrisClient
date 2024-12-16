package com.github.Debris.DebrisClient.mixin.compat.malilib;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.ProgressResume;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.GuiScrollBar;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiListBase.class, remap = false)
public abstract class GuiListBaseMixin<TYPE, WIDGET extends WidgetListEntryBase<TYPE>, WIDGETLIST extends WidgetListBase<TYPE, WIDGET>> extends GuiBase {
    @Shadow
    protected abstract WIDGETLIST getListWidget();

    @Unique
    private final GuiListBase instance = (GuiListBase) (Object) this;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (DCCommonConfig.ProgressResuming.getBooleanValue()) {
            WIDGETLIST listWidget = this.getListWidget();
            if (listWidget != null) {
                GuiScrollBar scrollbar = listWidget.getScrollbar();
                ProgressResume.getProgress(this.instance).ifPresent(scrollbar::setValue);
            }
        }
    }


}
