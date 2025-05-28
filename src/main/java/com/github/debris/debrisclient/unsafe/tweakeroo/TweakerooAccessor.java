package com.github.debris.debrisclient.unsafe.tweakeroo;

import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class TweakerooAccessor {
    @NotNull
    public static HitResult getRayTraceFromEntity(World worldIn, Entity entityIn, boolean useLiquids) {
        return RayTraceUtils.getRayTraceFromEntity(worldIn, entityIn, useLiquids);
    }

    public static boolean getFreeCamState() {
        return FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue();
    }

    public static void setFreeCamState(boolean state) {
        FeatureToggle.TWEAK_FREE_CAMERA.setBooleanValue(state);
    }

    public static void tryActivateFreeCam() {
        if (getFreeCamState()) return;
        setFreeCamState(true);
    }

    public static ClientPlayerEntity getCamEntity() {
        return CameraEntity.getCamera();
    }
}
