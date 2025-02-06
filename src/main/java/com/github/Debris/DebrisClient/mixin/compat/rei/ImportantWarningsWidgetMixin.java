package com.github.Debris.DebrisClient.mixin.compat.rei;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import me.shedaniel.rei.impl.client.gui.hints.ImportantWarningsWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ImportantWarningsWidget.class, remap = false)
public class ImportantWarningsWidgetMixin {
    @Shadow
    private static boolean dirty;

    @Shadow
    private boolean visible;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (DCCommonConfig.DisableREIWarning.getBooleanValue()) {
            dirty = false;
            this.visible = false;
        }
    }
}
