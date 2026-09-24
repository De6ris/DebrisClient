package com.github.debris.debrisclient.unsafe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class JeiAccess {
    public static IJeiRuntime jeiRuntime;

    @SuppressWarnings("OptionalIsPresent")
    public static ItemStack getHoveredStack() {
        ItemStack stack = jeiRuntime.getBookmarkOverlay().getItemStackUnderMouse();
        if (stack != null && !stack.isEmpty()) return stack;

        stack = jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse(VanillaTypes.ITEM_STACK);
        if (stack != null && !stack.isEmpty()) return stack;

        Optional<ItemStack> optional = jeiRuntime.getRecipesGui().getIngredientUnderMouse(VanillaTypes.ITEM_STACK);
        if (optional.isPresent()) return optional.get();

        return ItemStack.EMPTY;
    }
}
