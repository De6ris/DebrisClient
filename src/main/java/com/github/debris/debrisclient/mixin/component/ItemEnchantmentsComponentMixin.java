package com.github.debris.debrisclient.mixin.component;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEnchantments.class)
public class ItemEnchantmentsComponentMixin {
    @WrapOperation(method = "addToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;"))
    private Component appendMaxLevel(Holder<Enchantment> enchantment, int level, Operation<Component> original) {
        Component call = original.call(enchantment, level);
        if (DCCommonConfig.ExtraTooltip.getBooleanValue() && call instanceof MutableComponent mutableText) {
            int maxLevel = enchantment.value().getMaxLevel();
            if (level < maxLevel) {
                mutableText.append("/").append(Component.translatable("enchantment.level." + maxLevel));
            }
        }
        return call;
    }
}
