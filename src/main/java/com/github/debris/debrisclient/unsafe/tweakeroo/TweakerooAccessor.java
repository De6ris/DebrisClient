package com.github.debris.debrisclient.unsafe.tweakeroo;

import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class TweakerooAccessor {
    @NotNull
    public static HitResult getRayTraceFromEntity(World worldIn, Entity entityIn, boolean useLiquids) {
        return RayTraceUtils.getRayTraceFromEntity(worldIn, entityIn, useLiquids);
    }
}
