package com.github.debris.debrisclient.mixin.compat.jade;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.util.CullingUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.overlay.WailaTickHandler;

@Restriction(require = @Condition(ModReference.Jade))
@Mixin(value = WailaTickHandler.class, remap = false)
public class WailaTickHandlerMixin {
    @ModifyExpressionValue(method = "tickClient",
            at = @At(value = "INVOKE",
                    target = "Lsnownee/jade/api/config/IWailaConfig$General;shouldDisplayTooltip()Z",
                    remap = false),
            remap = false
    )
    private static boolean litematicaCompat(boolean original) {
        if (original && CullingUtil.shouldCullWaila()) return false;
        return original;
    }
}
