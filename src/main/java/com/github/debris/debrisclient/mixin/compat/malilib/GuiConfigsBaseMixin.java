package com.github.debris.debrisclient.mixin.compat.malilib;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.ProgressResume;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.GuiScrollBar;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiConfigsBase.class, remap = false)
public abstract class GuiConfigsBaseMixin extends GuiListBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> {
    @Unique
    private final GuiConfigsBase instance = (GuiConfigsBase) (Object) this;

    protected GuiConfigsBaseMixin(int listX, int listY) {
        super(listX, listY);
    }

    /// fail to mixin the initGui method here, so I create the mixin of GuiListBase
    @Inject(method = "removed", at = @At("RETURN"), remap = true)
    private void onRemoved(CallbackInfo ci) {
        if (DCCommonConfig.ProgressResuming.getBooleanValue()) {
            WidgetListConfigOptions listWidget = this.getListWidget();
            if (listWidget != null) {
                GuiScrollBar scrollbar = listWidget.getScrollbar();
                ProgressResume.saveProgress(this.instance, scrollbar.getValue());
            }
        }
    }
}
