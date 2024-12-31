package com.github.Debris.DebrisClient.mixin.compat.malilib;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.PinYinSupport;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(value = WidgetListBase.class, remap = false)
public class WidgetListBaseMixin<TYPE, WIDGET extends WidgetListEntryBase<TYPE>> {
    @Inject(method = "addFilteredContents", at = @At("HEAD"))
    private void onFilterCall(Collection<TYPE> entries, CallbackInfo ci) {
        if (DCCommonConfig.PinYinSearch.getBooleanValue()) {
            PinYinSupport.tryInit();
        }
    }

    @ModifyExpressionValue(method = "matchesFilter(Ljava/lang/String;Ljava/lang/String;)Z", at = @At(value = "INVOKE", target = "Ljava/lang/String;contains(Ljava/lang/CharSequence;)Z"))
    private boolean pinYinSearch(boolean original, @Local(argsOnly = true, ordinal = 0) String entryString, @Local(argsOnly = true, ordinal = 1) String filterText) {
        if (!DCCommonConfig.PinYinSearch.getBooleanValue()) return original;
        if (original) return true;
        return PinYinSupport.available() && PinYinSupport.matchesFilter(entryString, filterText);
    }
}
