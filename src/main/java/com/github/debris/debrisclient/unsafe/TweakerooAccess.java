package com.github.debris.debrisclient.unsafe;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import net.minecraft.client.network.ClientPlayerEntity;

public class TweakerooAccess {
    public static IConfigBoolean getFreeCamConfig() {
        return FeatureToggle.TWEAK_FREE_CAMERA;
    }

    public static ClientPlayerEntity getCamEntity() {
        return CameraEntity.getCamera();
    }
}
