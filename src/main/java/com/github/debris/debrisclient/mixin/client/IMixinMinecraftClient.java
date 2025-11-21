package com.github.debris.debrisclient.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface IMixinMinecraftClient {
    @Invoker("startUseItem")
    void invokeDoItemUse();

    @Invoker("startAttack")
    boolean invokeDoAttack();
}
