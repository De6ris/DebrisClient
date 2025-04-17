package com.github.Debris.DebrisClient.mixin.compat.minihud;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.unsafe.miniHud.MiniHudAccessor;
import fi.dy.masa.minihud.event.RenderHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Restriction(require = @Condition(ModReference.MiniHud))
@Mixin(value = RenderHandler.class, remap = false)
public class RenderHandlerMixin {
    @Shadow
    @Final
    private List<String> lines;

    @Inject(method = "updateLines", at = @At("RETURN"))
    private void onLinesUpdate(CallbackInfo ci) {
        MiniHudAccessor.onLinesUpdate(this.lines);
    }
}
