package com.github.Debris.DebrisClient.mixin.client.render;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GameRenderer.class, priority = 1002)
public class GameRendererMixin {
    @Redirect(method = "updateCrosshairTarget",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;"
            )
            , order = 10001
    )
    private Entity overrideCameraEntityForRayTrace(MinecraftClient mc) {
        // Return the real player for the hit target ray tracing if the
        // player inputs option is enabled in Free Camera mode.
        // Normally in Free Camera mode the Tweakeroo CameraEntity is set as the
        // render view/camera entity, which would then also ray trace from the camera point of view.
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue()
                && Configs.Generic.FREE_CAMERA_PLAYER_INPUTS.getBooleanValue()
                && !DCCommonConfig.ModifierFreeCamInput.getKeybind().isKeybindHeld()
                && mc.player != null) {
            return mc.player;
        }

        return mc.getCameraEntity();
    }
}
