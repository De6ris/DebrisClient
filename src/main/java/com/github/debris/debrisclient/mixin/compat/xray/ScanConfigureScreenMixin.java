package com.github.debris.debrisclient.mixin.compat.xray;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.mikey.xray.screens.ScanConfigureScreen;
import pro.mikey.xray.screens.helpers.GuiBase;

import java.awt.*;
import java.util.function.Supplier;

@Restriction(require = @Condition(ModReference.XRay))
@Mixin(ScanConfigureScreen.class)
public class ScanConfigureScreenMixin {
    @Unique
    private Color mapColor;

    @Inject(method = "<init>(Lnet/minecraft/world/level/block/Block;Ljava/util/function/Supplier;)V", at = @At("RETURN"))
    private void onInit(Block selectedBlock, Supplier<GuiBase> previousScreenCallback, CallbackInfo ci) {
        this.mapColor = new Color(selectedBlock.defaultMapColor().col);
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lpro/mikey/xray/screens/helpers/SliderWidget;<init>(IIIILjava/lang/String;D)V",
                    ordinal = 0
            ),
            index = 5
    )
    private double autoColorR(double value) {
        if (DCCommonConfig.XRayAutoColor.getBooleanValue()) {
            return this.mapColor.getRed() / 255.0D;
        }
        return value;
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lpro/mikey/xray/screens/helpers/SliderWidget;<init>(IIIILjava/lang/String;D)V",
                    ordinal = 1
            ),
            index = 5
    )
    private double autoColorG(double value) {
        if (DCCommonConfig.XRayAutoColor.getBooleanValue()) {
            return this.mapColor.getGreen() / 255.0D;
        }
        return value;
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lpro/mikey/xray/screens/helpers/SliderWidget;<init>(IIIILjava/lang/String;D)V",
                    ordinal = 2
            ),
            index = 5
    )
    private double autoColorB(double value) {
        if (DCCommonConfig.XRayAutoColor.getBooleanValue()) {
            return this.mapColor.getBlue() / 255.0D;
        }
        return value;
    }
}