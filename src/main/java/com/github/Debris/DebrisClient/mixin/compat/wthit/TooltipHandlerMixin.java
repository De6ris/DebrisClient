package com.github.Debris.DebrisClient.mixin.compat.wthit;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.unsafe.litematica.LitematicaAccessor;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TooltipHandler.class, remap = false)
public class TooltipHandlerMixin {
    @ModifyExpressionValue(method = "_tick",
            at = @At(value = "INVOKE",
                    target = "Lmcp/mobius/waila/config/WailaConfig$General;isDisplayTooltip()Z",
                    remap = false),
            remap = false
    )
    private static boolean litematicaCompat(boolean original) {
        if (!DCCommonConfig.WthitLitematicaCompat.getBooleanValue()) return original;
        if (!FabricLoader.getInstance().isModLoaded(ModReference.Litematica)) return original;
        if (original && LitematicaAccessor.isRenderingInfoOverlay()) return false;
        return original;
    }
}
