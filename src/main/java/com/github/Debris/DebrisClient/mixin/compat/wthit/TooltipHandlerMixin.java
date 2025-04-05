package com.github.Debris.DebrisClient.mixin.compat.wthit;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.util.CullingUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModReference.Wthit))
@Mixin(value = TooltipHandler.class, remap = false)
public class TooltipHandlerMixin {
    @ModifyExpressionValue(method = "_tick",
            at = @At(value = "INVOKE",
                    target = "Lmcp/mobius/waila/config/WailaConfig$General;isDisplayTooltip()Z",
                    remap = false),
            remap = false
    )
    private static boolean litematicaCompat(boolean original) {
        if (original && CullingUtil.shouldCullWthit()) return false;
        return original;
    }
}
