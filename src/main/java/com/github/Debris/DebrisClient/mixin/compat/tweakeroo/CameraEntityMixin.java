package com.github.Debris.DebrisClient.mixin.compat.tweakeroo;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CameraEntity.class, remap = false)
public class CameraEntityMixin {
    @WrapOperation(method = "createCameraEntity", at = @At(value = "INVOKE", target = "Lfi/dy/masa/tweakeroo/util/CameraEntity;refreshPositionAndAngles(DDDFF)V", remap = true), remap = false)
    private static void spectatorFix(CameraEntity instance, double x, double y, double z, float yaw, float pitch, Operation<Void> original) {
        if (DCCommonConfig.FreeCamSpectatorFix.getBooleanValue()) {
            Entity cameraEntity = MinecraftClient.getInstance().cameraEntity;
            if (cameraEntity != null) {
                x = cameraEntity.getX();
                y = cameraEntity.getY();
                z = cameraEntity.getZ();
                yaw = cameraEntity.getYaw();
                pitch = cameraEntity.getPitch();
            }
        }
        original.call(instance, x, y, z, yaw, pitch);
    }

    @WrapOperation(method = "createCameraEntity", at = @At(value = "INVOKE", target = "Lfi/dy/masa/tweakeroo/util/CameraEntity;setRotation(FF)V", remap = true), remap = false)
    private static void spectatorFix2(CameraEntity instance, float yaw, float pitch, Operation<Void> original) {
        if (DCCommonConfig.FreeCamSpectatorFix.getBooleanValue()) {
            Entity cameraEntity = MinecraftClient.getInstance().cameraEntity;
            if (cameraEntity != null) {
                yaw = cameraEntity.getYaw();
                pitch = cameraEntity.getPitch();
            }
        }
        original.call(instance, yaw, pitch);
    }
}
