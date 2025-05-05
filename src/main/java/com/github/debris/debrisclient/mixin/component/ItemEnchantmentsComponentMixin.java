package com.github.debris.debrisclient.mixin.component;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEnchantmentsComponent.class)
public class ItemEnchantmentsComponentMixin {
    @WrapOperation(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getName(Lnet/minecraft/registry/entry/RegistryEntry;I)Lnet/minecraft/text/Text;"))
    private Text appendMaxLevel(RegistryEntry<Enchantment> enchantment, int level, Operation<Text> original) {
        Text call = original.call(enchantment, level);
        if (DCCommonConfig.ExtraTooltip.getBooleanValue() && call instanceof MutableText mutableText) {
            int maxLevel = enchantment.value().getMaxLevel();
            if (level < maxLevel) {
                mutableText.append("/").append(Text.translatable("enchantment.level." + maxLevel));
            }
        }
        return call;
    }
}
