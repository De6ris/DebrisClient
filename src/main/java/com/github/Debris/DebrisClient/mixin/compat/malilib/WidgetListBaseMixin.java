package com.github.Debris.DebrisClient.mixin.compat.malilib;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.feat.PinYinSupport;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WidgetListBase.class, remap = false)
public class WidgetListBaseMixin<TYPE, WIDGET extends WidgetListEntryBase<TYPE>> {
    @ModifyExpressionValue(method = "matchesFilter(Ljava/lang/String;Ljava/lang/String;)Z", at = @At(value = "INVOKE", target = "Ljava/lang/String;contains(Ljava/lang/CharSequence;)Z"))
    private boolean pinYinSearch(boolean original, @Local(argsOnly = true, ordinal = 0) String entryString, @Local(argsOnly = true, ordinal = 1) String filterText) {
        if (!DCCommonConfig.PinYinSearch.getBooleanValue()) return original;
        if (original) return true;
        return PinYinSupport.available() && PinYinSupport.matchesFilter(entryString, filterText);
    }
}
