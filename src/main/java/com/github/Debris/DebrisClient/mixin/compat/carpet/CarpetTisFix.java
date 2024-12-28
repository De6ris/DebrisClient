package com.github.Debris.DebrisClient.mixin.compat.carpet;

import carpettisaddition.logging.loggers.microtiming.MicroTimingLoggerManager;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MicroTimingLoggerManager.class, remap = false)
public abstract class CarpetTisFix {
    @Shadow
    public static @NotNull MicroTimingLoggerManager getInstance() {
        return instance;
    }

    @Shadow
    private static MicroTimingLoggerManager instance;

    /**
     * @author Debris
     * @reason fix
     */
    @Overwrite
    public static void setCurrentWorld(ServerWorld world) {
        if (instance == null) return;
        instance.currentWorld.set(world);
    }
}
