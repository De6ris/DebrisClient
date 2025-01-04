package com.github.Debris.DebrisClient.unsafe.itemScroller;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.inventory.util.ItemUtil;
import fi.dy.masa.itemscroller.recipes.RecipePattern;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public abstract class AbstractMassCrafting {
    protected final RecipePattern selectedRecipe;
    protected ItemStack[] requiredArray;
    protected final ItemStack result;
    protected final HandledScreen<?> currentScreen;
    protected final ContainerSection playerInventory;
    protected final List<Slot> inputSlots;
    protected final Slot outputSlot;

    public AbstractMassCrafting() {
        this.selectedRecipe = RecipeStorage.getInstance().getSelectedRecipe();
        this.requiredArray = this.selectedRecipe.getRecipeItems();
        this.result = this.selectedRecipe.getResult();
        this.currentScreen = InventoryUtil.getGuiContainer();
        this.playerInventory = EnumSection.InventoryWhole.get();
        this.inputSlots = EnumSection.CraftMatrix.get().slots();
        this.outputSlot = EnumSection.CraftResult.get().slots().getFirst();
    }

    public abstract void run();

    // called as much as possible
    protected void dropAllProduct() {
        InventoryUtil.dropAllMatching(ItemUtil.predicateIDMeta(selectedRecipe.getResult()));
    }

    // Assuming checked
    protected void tryTakeResultSlot() {
        InventoryUtil.quickMove(outputSlot);// move result to inventory

        if (!outputSlot.hasStack()) return; // success moving to inventory

        if (DCCommonConfig.Use64Q.getBooleanValue()) {
            do {
                InventoryUtil.dropStack(outputSlot);
            } while (outputSlot.hasStack());
        } else {
            for (Slot slot : playerInventory.slots()) {
                InventoryUtil.dropStack(slot);
                InventoryUtil.quickMove(outputSlot);// move result to inventory
                if (!outputSlot.hasStack()) {// success
                    dropAllProduct();
                    break;
                }
            }
        }

    }

    protected boolean checkResultSlot() {
        return ItemUtil.compareIDMeta(outputSlot.getStack(), result);
    }
}
