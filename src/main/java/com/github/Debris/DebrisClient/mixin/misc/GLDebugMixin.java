package com.github.Debris.DebrisClient.mixin.misc;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gl.GlDebug;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GlDebug.class)
public class GLDebugMixin {
    @WrapWithCondition(method = "info", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private static boolean mute(Logger instance, String s, Object o) {
        return !DCCommonConfig.MuteGLDebugInfo.getBooleanValue();
    }
}
