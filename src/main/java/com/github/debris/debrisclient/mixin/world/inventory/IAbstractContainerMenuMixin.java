package com.github.debris.debrisclient.mixin.world.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerMenu.class)
public interface IAbstractContainerMenuMixin {
    @Accessor
    MenuType<?> getMenuType();
}
