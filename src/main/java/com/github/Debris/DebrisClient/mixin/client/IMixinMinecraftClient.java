package com.github.Debris.DebrisClient.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface IMixinMinecraftClient {
    @Invoker("doItemUse")
    void invokeDoItemUse();

    @Invoker("doAttack")
    boolean invokeDoAttack();
}
