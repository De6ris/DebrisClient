package com.github.debris.debrisclient.unsafe.itemScroller;

import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.EnumSection;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.ItemUtil;
import fi.dy.masa.itemscroller.recipes.RecipePattern;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AbstractMassCrafting {
    protected final RecipePattern selectedRecipe;
    protected ItemStack[] requiredArray;
    protected final ItemStack result;
    protected final AbstractRecipeBookScreen<?> currentScreen;
    protected final ContainerSection playerInventory;
    protected final List<Slot> inputSlots;
    protected final Slot outputSlot;

    public AbstractMassCrafting() {
        this.selectedRecipe = RecipeStorage.getInstance().getSelectedRecipe();
        this.requiredArray = this.selectedRecipe.getRecipeItems();
        this.result = this.selectedRecipe.getResult();
        this.currentScreen = (AbstractRecipeBookScreen<?>) InventoryUtil.getGuiContainer();
        this.playerInventory = EnumSection.InventoryWhole.get();
        this.inputSlots = EnumSection.CraftMatrix.get().slots();
        this.outputSlot = EnumSection.CraftResult.get().getFirstSlot();
    }

    public abstract void run();

    // called as much as possible
    protected void dropAllProduct() {
        InventoryUtil.dropAllMatching(ItemUtil.predicateIDMeta(selectedRecipe.getResult()));
    }

    // Assuming checked
    protected void tryTakeResultSlot() {
        InventoryUtil.dropStack(outputSlot);
    }

    protected boolean checkResultSlot() {
        return ItemUtil.compareIDMeta(outputSlot.getItem(), result);
    }
}
