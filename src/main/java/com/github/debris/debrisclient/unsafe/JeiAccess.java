package com.github.debris.debrisclient.unsafe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.ItemStack;

public class JeiAccess {
    public static IJeiRuntime jeiRuntime;

    public static ItemStack getHoveredStack() {
        ItemStack stack = jeiRuntime.getBookmarkOverlay().getItemStackUnderMouse();
        if (stack != null && !stack.isEmpty()) return stack;
        stack = jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse(VanillaTypes.ITEM_STACK);
        if (stack != null && !stack.isEmpty()) return stack;
        return ItemStack.EMPTY;
    }
}
