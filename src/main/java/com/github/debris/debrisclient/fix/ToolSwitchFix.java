package com.github.debris.debrisclient.fix;

import fi.dy.masa.malilib.util.EquipmentUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class ToolSwitchFix {
    public static boolean compareMaterial(ItemStack testedStack, ItemStack previousTool, BlockState state, Function<ItemStack, Integer> weightAccess) {
        if (!EquipmentUtils.isCorrectTool(testedStack, state)) {
            return false;
        }
        return weightAccess.apply(testedStack) > weightAccess.apply(previousTool);
    }
}
