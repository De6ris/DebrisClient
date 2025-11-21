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
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModReference.Tweakeroo))
@Mixin(value = CameraEntity.class, remap = false)
public class CameraEntityMixin {
    @ModifyReturnValue(method = "createCameraEntity", at = @At("RETURN"), remap = false)
    private static CameraEntity retroFreeCam(CameraEntity camera, @Local(argsOnly = true) Minecraft client) {
        if (DCCommonConfig.RetroFreeCam.getBooleanValue()) {
            Entity view = client.getCameraEntity();
            if (view == null) return camera;
            float yaw = view.getYRot();
            float pitch = view.getXRot();

            camera.snapTo(view.getX(), view.getY(), view.getZ(), yaw, pitch);

            camera.setYRot(yaw % 360.0F);
            camera.setXRot(pitch % 360.0F);
        }
        return camera;
    }

    @WrapOperation(method = "createCameraEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;position()Lnet/minecraft/world/phys/Vec3;",
                    remap = true),
            remap = false)
    private static Vec3 spectatorFix(LocalPlayer instance, Operation<Vec3> original) {
        if (DCCommonConfig.FreeCamSpectatorFix.getBooleanValue()) {
            Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
            if (cameraEntity != null) {
                return cameraEntity.position();
            }
        }
        return original.call(instance);
    }

    @WrapOperation(method = "createCameraEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getYRot()F",
                    remap = true),
            remap = false)
    private static float spectatorFix1(LocalPlayer instance, Operation<Float> original) {
        if (DCCommonConfig.FreeCamSpectatorFix.getBooleanValue()) {
            Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
            if (cameraEntity != null) {
                return cameraEntity.getYRot();
            }
        }
        return original.call(instance);
    }

    @WrapOperation(method = "createCameraEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getXRot()F",
                    remap = true),
            remap = false)
    private static float spectatorFix2(LocalPlayer instance, Operation<Float> original) {
        if (DCCommonConfig.FreeCamSpectatorFix.getBooleanValue()) {
            Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
            if (cameraEntity != null) {
                return cameraEntity.getXRot();
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
