package com.github.debris.debrisclient.mixin.world;

import com.github.debris.debrisclient.feat.log.GameLogs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.dimension.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PortalForcer.class)
public class PortalForcerMixin {
    @Shadow
    @Final
    private ServerWorld world;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Inject(method = "createPortal", at = @At(value = "RETURN", ordinal = 1))
    private void onPortalCreated(BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<Optional<BlockLocating.Rectangle>> cir) {
        GameLogs.PORTAL.onPortalCreated(this.world, cir.getReturnValue().get().lowerLeft);
    }
}
