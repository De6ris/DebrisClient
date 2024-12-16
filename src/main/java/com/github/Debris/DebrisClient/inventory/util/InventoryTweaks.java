package com.github.Debris.DebrisClient.inventory.util;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import com.github.Debris.DebrisClient.inventory.sort.SortCategory;
import com.github.Debris.DebrisClient.util.PermutationUtil;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class InventoryTweaks {
    public static boolean trySort() {
        Optional<ContainerSection> optional = SectionHandler.getSectionMouseOver();
        if (optional.isEmpty()) return false;
        ContainerSection section = optional.get();
        if (!shouldSort(section)) return false;
        int before = InventoryUtil.getChangeCount();
        makeSureNotHoldingItem(section);
        section.mergeSlots();
        sortInternal(section);
        int after = InventoryUtil.getChangeCount();
        return after != before;// seen as sort success
    }

    private static final EnumSet<EnumSection> SortBlackList = EnumSet.of(
            EnumSection.FakePlayerActions,
            EnumSection.FakePlayerEnderChestActions
    );

    private static boolean shouldSort(ContainerSection section) {
        for (EnumSection enumSection : SortBlackList) {
            if (enumSection.isOf(section)) return false;
        }
        return true;
    }

    // try to put held item to this section, if fail then drop
    public static void makeSureNotHoldingItem(ContainerSection section) {
        ItemStack heldItem = InventoryUtil.getHeldStack();
        if (heldItem.isEmpty()) return;
        Optional<Slot> mergeSlot = section.absorbsOneScroll(heldItem);
        while (mergeSlot.isPresent()) {
            InventoryUtil.leftClick(mergeSlot.get());
            heldItem = InventoryUtil.getHeldStack();
            if (heldItem.isEmpty()) {
                return;// merge success
            } else {
                mergeSlot = section.absorbsOneScroll(heldItem);// try merge to other slot
            }
        }
        if (InventoryUtil.isHoldingItem()) {// if still
            Optional<Slot> emptySlot = section.getEmptySlot();
            if (emptySlot.isPresent()) {
                InventoryUtil.leftClick(emptySlot.get());// put held to empty
            } else {
                InventoryUtil.dropHeldItem();// just drop
            }
        }
    }

    // assume no holding item, all slots are well merged, but still blanks between
    private static void sortInternal(ContainerSection section) {
        Comparator<ItemStack> itemStackSorter = SortCategory.getItemStackSorter();

        Comparator<Slot> slotSorter = (x, y) -> itemStackSorter.compare(x.getStack(), y.getStack());

        BiConsumer<Slot, Slot> swapAction = InventoryUtil::swapSlots;
//                    DebrisClient.logger.info("swapping the {} and {}", j, j + 1);

        if (DCCommonConfig.SortingBoxesLast.getBooleanValue()) {
            putBoxesLast(section);

            Map<Boolean, List<Slot>> grouped = section.slots().stream().filter(Slot::hasStack).collect(Collectors.partitioningBy(x -> isShulkerBox(x.getStack())));

            List<Slot> nonBoxes = grouped.get(false);
            section.subSection(nonBoxes).fillBlanks();

            Slot[] nonBoxesArray = nonBoxes.stream().filter(Slot::hasStack).toArray(Slot[]::new);

            Slot[] boxes = grouped.get(true).toArray(Slot[]::new);

            runSorting(nonBoxesArray, slotSorter, swapAction);
            runSorting(boxes, slotSorter, swapAction);
        } else {
            section.fillBlanks();
            Slot[] nonEmptySlots = section.slots().stream().filter(Slot::hasStack).toArray(Slot[]::new);
            runSorting(nonEmptySlots, slotSorter, swapAction);
        }
    }

    private static void putBoxesLast(ContainerSection section) {
        List<Slot> slots = section.slots();
        int iterateStart = slots.size() - 1;
        for (int i = iterateStart; i >= 0; i--) {// inverse order reduce operations
            if (i == iterateStart) continue;// skip the first
            Slot slot = slots.get(i);
            if (!isShulkerBox(slot.getStack())) continue;// skip those not box
            moveToNextNonBox(section, i, slot);
        }

        // now fill in the blanks
        int theIndexNonBox = iterateStart;
        for (int i = iterateStart; i >= 0; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (isShulkerBox(slot.getStack())) continue;// skip those box
            theIndexNonBox = i;
            break;
        }
        List<Slot> theFormerPart = section.slots().subList(0, theIndexNonBox + 1);
        section.subSection(theFormerPart).fillBlanks();
    }

    private static void moveToNextNonBox(ContainerSection section, int index, Slot currentSlot) {
        List<Slot> slots = section.slots();
        for (int i = slots.size() - 1; i > index; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (isShulkerBox(slot.getStack())) continue;// skip those are box
            if (slot.hasStack()) {
                InventoryUtil.swapSlots(slot, currentSlot);
            } else {
                InventoryUtil.moveToEmpty(currentSlot, slot);
            }
            break;
        }
    }

    public static boolean isShulkerBox(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock;
    }

    /*
     * sorter: if j is bigger than j+1, I will swap them
     * */
    private static <T> void runSorting(T[] slots, Comparator<T> sorter, BiConsumer<T, T> swapAction) {
        int length = slots.length;
        if (length <= 1) return;

        if (DCCommonConfig.CachedSorting.getBooleanValue()) {
            List<PermutationUtil.Transposition> optimal = PermutationUtil.getOptimalProcess(slots, sorter);
            optimal.forEach(x -> x.operate(slots, swapAction));
        } else {
            // direct sorting
            for (int i = 1; i < length; i++) {
                boolean flag = true;
                for (int j = 0; j < length - i; j++) {
                    T slotJ = slots[j];
                    T slotJ_1 = slots[j + 1];
                    int compare = sorter.compare(slotJ, slotJ_1);
                    if (compare > 0) {
                        swapAction.accept(slotJ, slotJ_1);
                        flag = false;
                    }
                }
                if (flag) break;
            }
        }
    }
}
