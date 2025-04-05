package com.github.Debris.DebrisClient.mixin.compat.xray;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.mikey.fabric.xray.screens.forge.GuiAddBlock;
import pro.mikey.fabric.xray.screens.forge.GuiBase;

import java.awt.*;
import java.util.function.Supplier;

@Restriction(require = @Condition(ModReference.XRay))
@Mixin(GuiAddBlock.class)
public class GuiAddBlockMixin {
    @Shadow
    private BlockState selectBlock;

    @Unique
    private Color mapColor;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(BlockState selectedBlock, Supplier<GuiBase> previousScreenCallback, CallbackInfo ci) {
        this.mapColor = new Color(this.selectBlock.getMapColor(null, null).color);
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lpro/mikey/fabric/xray/screens/forge/RatioSliderWidget;<init>(IIIILnet/minecraft/text/Text;D)V", ordinal = 0), index = 5)
    private double autoColorR(double value) {
        if (DCCommonConfig.XRayAutoColor.getBooleanValue()) {
            return this.mapColor.getRed() / 255.0D;
        }
        return value;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lpro/mikey/fabric/xray/screens/forge/RatioSliderWidget;<init>(IIIILnet/minecraft/text/Text;D)V", ordinal = 1), index = 5)
    private double autoColorG(double value) {
        if (DCCommonConfig.XRayAutoColor.getBooleanValue()) {
            return this.mapColor.getGreen() / 255.0D;
        }
        return value;
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lpro/mikey/fabric/xray/screens/forge/RatioSliderWidget;<init>(IIIILnet/minecraft/text/Text;D)V", ordinal = 2), index = 5)
    private double autoColorB(double value) {
        if (DCCommonConfig.XRayAutoColor.getBooleanValue()) {
            return this.mapColor.getBlue() / 255.0D;
        }
        return value;
    }
}
