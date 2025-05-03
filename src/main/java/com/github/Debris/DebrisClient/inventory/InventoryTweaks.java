package com.github.Debris.DebrisClient.inventory;

import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import com.github.Debris.DebrisClient.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.ItemUtil;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;

public class InventoryTweaks {

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

    public static boolean tryThrowSection() {
        Optional<ContainerSection> section = SectionHandler.getSectionMouseOver();
        if (section.isEmpty()) return false;
        section.get().notEmptyRun(InventoryUtil::dropStack);
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
}
