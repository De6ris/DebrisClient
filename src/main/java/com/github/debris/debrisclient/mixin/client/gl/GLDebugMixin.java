package com.github.debris.debrisclient.mixin.client.gl;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.opengl.GlDebug;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GlDebug.class)
public class GLDebugMixin {
    @WrapWithCondition(method = "printDebugLog", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private boolean mute(Logger instance, String s, Object o) {
        return !DCCommonConfig.MuteGLDebugInfo.getBooleanValue();
    }
}
