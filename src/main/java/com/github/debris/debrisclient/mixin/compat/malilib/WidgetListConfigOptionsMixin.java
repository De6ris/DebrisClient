package com.github.debris.debrisclient.mixin.compat.malilib;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = WidgetListConfigOptions.class, remap = false)
public class WidgetListConfigOptionsMixin {
    @Inject(method = "getEntryStringsForFilter(Lfi/dy/masa/malilib/gui/GuiConfigsBase$ConfigOptionWrapper;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z", ordinal = 0))
    private void onAdd(GuiConfigsBase.ConfigOptionWrapper entry, CallbackInfoReturnable<List<String>> cir, @Local IConfigBase config, @Local ArrayList<String> list) {
        if (DCCommonConfig.CommentSearch.getBooleanValue()) {
            list.add(config.getComment());
        }
    }
}
