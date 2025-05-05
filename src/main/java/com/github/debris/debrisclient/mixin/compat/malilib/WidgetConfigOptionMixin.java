package com.github.debris.debrisclient.mixin.compat.malilib;

import com.github.debris.debrisclient.gui.button.ConfigButtonTrigger;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.config.api.IConfigTrigger;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import fi.dy.masa.malilib.hotkeys.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WidgetConfigOption.class, remap = false)
public abstract class WidgetConfigOptionMixin extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper> {
    public WidgetConfigOptionMixin(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex) {
        super(x, y, width, height, parent, entry, listIndex);
    }

    @Inject(method = "addHotkeyConfigElements", at = @At("RETURN"))
    private void addFeature(int x, int y, int configWidth, String configName, IHotkey hotkey, CallbackInfo ci) {
        if (hotkey instanceof IConfigTrigger) {
            IKeybind keybind = hotkey.getKeybind();
            IHotkeyCallback callback = ((KeybindMulti) keybind).getCallback();
            if (callback == null) return;
            this.addButton(new ConfigButtonTrigger(x + DCCommonConfig.TriggerButtonOffset.getIntegerValue(), y), (buttonBase, i) -> {
                if (i == 0) {
                    boolean cancel = callback.onKeyAction(KeyAction.PRESS, keybind);
                    if (cancel) return;
                    callback.onKeyAction(KeyAction.RELEASE, keybind);
                }
            });
        }
    }
}
