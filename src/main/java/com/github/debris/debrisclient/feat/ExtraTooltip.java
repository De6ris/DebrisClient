package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ExtraTooltip {
    public static void onTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, @Nullable Player player, TooltipFlag type, Consumer<Component> list) {
        if (!DCCommonConfig.ExtraTooltip.getBooleanValue()) return;
        if (type.isAdvanced()) {
            appendRepairCost(stack, context, list);
            appendEnchantmentCost(stack, context, list);
        }
    }

    private static void appendRepairCost(ItemStack stack, Item.TooltipContext context, Consumer<Component> list) {
        Integer cost = stack.getOrDefault(DataComponents.REPAIR_COST, 0);
        if (cost > 0) {
            list.accept(Component.literal(String.format("铁砧惩罚: %d", cost)).withStyle(ChatFormatting.GRAY));
        }
    }

    private static void appendEnchantmentCost(ItemStack stack, Item.TooltipContext context, Consumer<Component> list) {
        boolean bl = stack.has(DataComponents.STORED_ENCHANTMENTS);// see AnvilScreenHandler#updateResult
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        if (!enchantments.isEmpty()) {
            int cost = enchantments.entrySet().stream()
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
            list.accept(Component.literal("附魔价值: " + cost).withStyle(ChatFormatting.GRAY));
        }
    }
}
