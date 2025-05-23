package com.github.debris.debrisclient.mixin.compat.tweakeroo;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModReference.Tweakeroo))
@Mixin(value = CameraEntity.class, remap = false)
public class CameraEntityMixin {
    @ModifyReturnValue(method = "createCameraEntity", at = @At("RETURN"), remap = false)
    private static CameraEntity retroFreeCam(CameraEntity camera, @Local(argsOnly = true) MinecraftClient client) {
        if (DCCommonConfig.RetroFreeCam.getBooleanValue()) {
            Entity view = client.cameraEntity;
            if (view == null) return camera;
            float yaw = view.getYaw();
            float pitch = view.getPitch();

            camera.refreshPositionAndAngles(view.getX(), view.getY(), view.getZ(), yaw, pitch);

            camera.setYaw(yaw % 360.0F);
            camera.setPitch(pitch % 360.0F);
        }
        return camera;
    }

    @WrapOperation(method = "createCameraEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPos()Lnet/minecraft/util/math/Vec3d;",
                    remap = true),
            remap = false)
    private static Vec3d spectatorFix(ClientPlayerEntity instance, Operation<Vec3d> original) {
        if (DCCommonConfig.FreeCamSpectatorFix.getBooleanValue()) {
            Entity cameraEntity = MinecraftClient.getInstance().cameraEntity;
            if (cameraEntity != null) {
                return cameraEntity.getPos();
            }
        }
        return original.call(instance);
    }

    @WrapOperation(method = "createCameraEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F",
                    remap = true),
            remap = false)
    private static float spectatorFix1(ClientPlayerEntity instance, Operation<Float> original) {
        if (DCCommonConfig.FreeCamSpectatorFix.getBooleanValue()) {
            Entity cameraEntity = MinecraftClient.getInstance().cameraEntity;
            if (cameraEntity != null) {
                return cameraEntity.getYaw();
            }
        }
        return original.call(instance);
    }

    @WrapOperation(method = "createCameraEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F",
                    remap = true),
            remap = false)
    private static float spectatorFix2(ClientPlayerEntity instance, Operation<Float> original) {
        if (DCCommonConfig.FreeCamSpectatorFix.getBooleanValue()) {
            Entity cameraEntity = MinecraftClient.getInstance().cameraEntity;
            if (cameraEntity != null) {
                return cameraEntity.getPitch();
            }
        }
        return original.call(instance);
    }

//    @ModifyArg(method = "createCameraEntity", at = @At(value = "INVOKE", target = "Lfi/dy/masa/tweakeroo/util/CameraEntity;setPos(DDD)V"), index = 1)
//    private static double retroPos(double y) {
//        if (DCCommonConfig.RetroFreeCam.getBooleanValue()) {
//            y -= 0.125;
//        }
//        return y;
//    }
}
