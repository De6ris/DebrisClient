package com.github.debris.debrisclient.unsafe;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import net.minecraft.client.player.LocalPlayer;

public class TweakerooAccess {
    public static IConfigBoolean getFreeCamConfig() {
        return FeatureToggle.TWEAK_FREE_CAMERA;
    }

    public static LocalPlayer getCamEntity() {
        return CameraEntity.getCamera();
    }
}
