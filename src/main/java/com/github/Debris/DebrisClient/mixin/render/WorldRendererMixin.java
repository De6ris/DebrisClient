package com.github.Debris.DebrisClient.mixin.render;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.render.PathNodesRenderer;
import com.github.Debris.DebrisClient.render.RenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    private @Nullable ClientWorld world;

    @Inject(method = "hasBlindnessOrDarkness", at = @At("HEAD"), cancellable = true)
    private void override(Camera camera, CallbackInfoReturnable<Boolean> cir) {
        if (DCCommonConfig.DarknessOverride.getBooleanValue()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4fStack;popMatrix()Lorg/joml/Matrix4fStack;", remap = false))
    private void onRenderWorldPost(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        PathNodesRenderer.getInstance().onRenderWorldPost(this.world, RenderContext.ofWorld(matrix4f, matrix4f2), tickCounter.getTickDelta(false));
    }
}
