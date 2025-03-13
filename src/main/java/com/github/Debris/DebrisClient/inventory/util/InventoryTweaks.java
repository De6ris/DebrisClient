package com.github.Debris.DebrisClient.inventory.util;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import com.github.Debris.DebrisClient.inventory.sort.SortCategory;
import com.github.Debris.DebrisClient.util.PermutationUtil;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BundleItem;
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
            if (section.isOf(enumSection)) return false;
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

        if (DCCommonConfig.SortingContainersLast.getBooleanValue()) {
            putContainersLast(section);

            Map<Boolean, List<Slot>> grouped = section.slots().stream().filter(Slot::hasStack).collect(Collectors.partitioningBy(x -> isContainer(x.getStack())));

            Slot[] nonContainers = grouped.get(false).toArray(Slot[]::new);
            Slot[] containers = grouped.get(true).toArray(Slot[]::new);

            runSorting(nonContainers, slotSorter, swapAction);
            runSorting(containers, slotSorter, swapAction);
        } else {
            section.fillBlanks();
            section.mergeSlots();
            section.fillBlanks();
            Slot[] nonEmptySlots = section.slots().stream().filter(Slot::hasStack).toArray(Slot[]::new);
            runSorting(nonEmptySlots, slotSorter, swapAction);
        }
    }

    private static void putContainersLast(ContainerSection section) {
        List<Slot> slots = section.slots();
        int iterateStart = slots.size() - 1;
        for (int i = iterateStart; i >= 0; i--) {// inverse order reduce operations
            if (i == iterateStart) continue;// skip the first
            Slot slot = slots.get(i);
            if (!isContainer(slot.getStack())) continue;// skip those not container
            moveToNextNonContainer(section, i, slot);
        }

        // now fill in the blanks
        int theIndexNonContainer = iterateStart;
        for (int i = iterateStart; i >= 0; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (isContainer(slot.getStack())) continue;// skip those containers
            theIndexNonContainer = i;
            break;
        }
        ContainerSection theFormerPart = section.subSection(0, theIndexNonContainer + 1);
        theFormerPart.fillBlanks();
        theFormerPart.mergeSlots();
        theFormerPart.fillBlanks();
    }

    private static void moveToNextNonContainer(ContainerSection section, int index, Slot currentSlot) {
        List<Slot> slots = section.slots();
        for (int i = slots.size() - 1; i > index; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (isContainer(slot.getStack())) continue;// skip those containers
            if (slot.hasStack()) {
                InventoryUtil.swapSlots(slot, currentSlot);
            } else {
                InventoryUtil.moveToEmpty(currentSlot, slot);
            }
            break;
        }
    }

    public static boolean isContainer(ItemStack itemStack) {
        if (isShulkerBox(itemStack)) return true;
        if (isBundle(itemStack)) return true;
        return false;
    }

    public static boolean isShulkerBox(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock;
    }

    public static boolean isBundle(ItemStack itemStack) {
        return itemStack.getItem() instanceof BundleItem;
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

    public static boolean trySpreading(boolean rightClick) {
        if (!InventoryUtil.isHoldingItem()) return false;

        Optional<ContainerSection> sectionOptional = SectionHandler.getSectionMouseOver();
        if (sectionOptional.isEmpty()) return false;
        ContainerSection section = sectionOptional.get();

        InventoryUtil.startSpreading(rightClick);
        section.allRun(x -> InventoryUtil.addToSpreading(x, rightClick));
        InventoryUtil.finishSpreading(rightClick);

        return true;
    }

    public static void tryMoveSimilar() {
        InventoryUtil.getSlotMouseOver().ifPresent(slot -> {
            if (slot.hasStack()) {
                ItemStack template = slot.getStack().copy();
                ContainerSection section = SectionHandler.getSection(slot);
                section = expandSectionIfPossible(section);
                section.predicateRun(ItemUtil.predicateIDMeta(template), InventoryUtil::quickMove);
            }
        });
    }

    private static ContainerSection expandSectionIfPossible(ContainerSection section) {
        if (GuiUtils.getCurrentScreen() instanceof InventoryScreen) return section;
        if (section.isOf(EnumSection.InventoryHotBar) || section.isOf(EnumSection.InventoryStorage))
            return EnumSection.InventoryWhole.get();
        return section;
    }

    public static boolean tryClearBundle() {
        ItemStack heldStack = InventoryUtil.getHeldStack();

        if (heldStack.isEmpty()) {// empty hand, try clear cursor bundle
            Optional<Slot> slotMouseOver = InventoryUtil.getSlotMouseOver();
            if (slotMouseOver.isPresent()) {
                Slot slot = slotMouseOver.get();
                if (isBundle(slot.getStack())) {
                    clearCursorBundle(slot);
                    return true;
                }
            }
            return false;
        }

        if (isBundle(heldStack)) {
            Optional<ContainerSection> sectionOptional = SectionHandler.getSectionMouseOver();
            if (sectionOptional.isPresent()) {
                clearHeldBundle(sectionOptional.get());
                return true;
            }
        }

        return false;
    }

    private static void clearHeldBundle(ContainerSection section) {
        section.emptyRun(InventoryUtil::rightClick);
    }

    private static void clearCursorBundle(Slot slot) {
        InventoryUtil.leftClick(slot);// take up
        ContainerSection section = SectionHandler.getSection(slot);
        section.emptyRun(InventoryUtil::rightClick);
        if (slot.getStack().isEmpty()) InventoryUtil.leftClick(slot);// put down
    }

    public static boolean tryThrowSection() {
        Optional<ContainerSection> section = SectionHandler.getSectionMouseOver();
        if (section.isEmpty()) return false;
        section.get().notEmptyRun(InventoryUtil::dropStack);
        return true;
    }

    /**
     * @return Those simple containers that own one single section.
     */
    public static Optional<ContainerSection> getChestSection() {
        List<ContainerSection> sections = SectionHandler.getUnIdentifiedSections();
        if (sections.size() == 1) {
            ContainerSection section = sections.getFirst();
            return Optional.of(section);
        }
        return Optional.empty();
    }
}
