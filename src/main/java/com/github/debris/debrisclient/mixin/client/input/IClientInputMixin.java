package com.github.debris.debrisclient.mixin.client.input;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientInput.class)
public interface IClientInputMixin {
    @Accessor
    void setMoveVector(Vec2 vec2);
}
