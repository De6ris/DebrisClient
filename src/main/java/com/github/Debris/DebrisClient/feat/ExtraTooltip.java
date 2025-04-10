package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ExtraTooltip {
    public static void onTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, TooltipType type, Consumer<Text> list) {
        if (!DCCommonConfig.ExtraTooltip.getBooleanValue()) return;
        if (type.isAdvanced()) {
            appendRepairCost(stack, context, list);
            appendEnchantmentCost(stack, context, list);
        }
    }

    private static void appendRepairCost(ItemStack stack, Item.TooltipContext context, Consumer<Text> list) {
        Integer cost = stack.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
        if (cost > 0) {
            list.accept(Text.literal(String.format("修复成本: %d", cost)).formatted(Formatting.GRAY));
        }
    }

    private static void appendEnchantmentCost(ItemStack stack, Item.TooltipContext context, Consumer<Text> list) {
        boolean bl = stack.contains(DataComponentTypes.STORED_ENCHANTMENTS);// see AnvilScreenHandler#updateResult
        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
        if (!enchantments.isEmpty()) {
            int cost = enchantments.getEnchantmentEntries().stream()
                    .mapToInt(x -> {
                        Enchantment enchantment = x.getKey().value();
                        int r = x.getIntValue();
                        if (r > enchantment.getMaxLevel()) {
                            r = enchantment.getMaxLevel();
                        }
                        int s = enchantment.getAnvilCost();
                        if (bl) s = Math.max(1, s / 2);
                        return s * r;
                    }).sum();
            list.accept(Text.literal("附魔成本: " + cost).formatted(Formatting.GRAY));
        }
    }
}
