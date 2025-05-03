package com.github.Debris.DebrisClient.unsafe.itemScroller;

import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.ItemUtil;
import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MassCraftingManual extends AbstractMassCrafting {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void run() {
        if (currentScreen instanceof InventoryScreen) {// 2x2 crafting
            if (selectedRecipe.getRecipeLength() == 9) {
                Optional<List<Integer>> pattern = is2x2RecipeIn3x3(requiredArray);
                if (pattern.isPresent()) {
                    requiredArray = cast3x3RecipeTo2x2(requiredArray, pattern.get());
                } else {
                    return;
                }
            }
        }

        for (int i = 0; i < inputSlots.size(); i++) {// clear the crafting matrix
            Slot slot = inputSlots.get(i);
            if (slot.hasStack()) {
                if (ItemUtil.compareIDMeta(slot.getStack(), requiredArray[i]))
                    continue;// means this is ready for crafting
                InventoryUtil.quickMove(slot);
                InventoryUtil.dropStackIfPossible(slot);// if we can not move to inventory, just throw
            }
        }

        if (!checkResultSlot() && !canSupplyToResultByMoving()) {// no result and no supply
            new MassCraftingRecipeBook().run();// use this to spread items, avoid boring item dividing
            return;
        }

        int loop = 0;
        while (checkResultSlot() || canSupplyToResultByMoving()) {// has result or has supply

            dropAllProduct();

            for (int i = 0; i < requiredArray.length; i++) {
                ItemStack requiredItem = requiredArray[i];
                if (requiredItem.isEmpty()) continue;// won't supply empty
                Slot slot = inputSlots.get(i);
                if (slot.hasStack()) continue;// means ready for crafting
                trySupplySlot(slot, requiredItem, playerInventory);
            }

            if (checkResultSlot()) {
                tryTakeResultSlot();
            }

            loop++;
            if (loop > 10) {
                LOGGER.warn("try mass crafting: why loop over 10 times");
                break;
            }
        }
    }

    private boolean canSupplyToResultByMoving() {
        List<Slot> clone = new ArrayList<>(playerInventory.slots().stream().filter(Slot::hasStack).toList());
        for (int i = 0; i < requiredArray.length; i++) {
            ItemStack requiredItem = requiredArray[i];
            if (requiredItem.isEmpty()) continue;// won't supply empty
            Slot slot = inputSlots.get(i);
            if (slot.hasStack()) continue;// means ready for crafting
            Optional<Slot> optional = clone.stream().filter(x -> x.hasStack() && ItemUtil.compareIDMeta(x.getStack(), requiredItem)).findFirst();
            if (optional.isPresent()) {
                clone.remove(optional.get());// this slot is valid, then remove it from list
                continue;
            }
            return false;
        }
        return true;
    }

    private static void trySupplySlot(Slot matrixEntry, ItemStack requiredItem, ContainerSection playerInventory) {
        playerInventory.findItem(requiredItem).ifPresent(x -> InventoryUtil.moveToEmpty(x, matrixEntry));
    }

    private static ItemStack[] cast3x3RecipeTo2x2(ItemStack[] itemStacks, List<Integer> pattern) {
        ItemStack[] newArray = new ItemStack[4];
        int index = 0;
        for (int i = 0; i < itemStacks.length; i++) {
            if (pattern.contains(i)) continue;
            newArray[index] = itemStacks[i];
            index++;
        }
        return newArray;
    }

    // returns the pattern that those places are empty
    private static Optional<List<Integer>> is2x2RecipeIn3x3(ItemStack[] itemStacks) {
        return PatternPredicates.stream().filter(x -> satisfyPattern(itemStacks, x)).findFirst();
    }

    // check if these positions are empty
    private static boolean satisfyPattern(ItemStack[] itemStacks, List<Integer> list) {
        return list.stream().allMatch(x -> itemStacks[x].isEmpty());
    }

    private static final ImmutableSet<List<Integer>> PatternPredicates = ImmutableSet.of(
            List.of(2, 5, 6, 7, 8),
            List.of(0, 3, 6, 7, 8),
            List.of(0, 1, 2, 5, 8),
            List.of(0, 1, 2, 3, 6)
    );
}
