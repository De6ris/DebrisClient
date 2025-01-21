package com.github.Debris.DebrisClient.inventory.util;

import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.util.AccessorUtil;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class InventoryUtil {
    public static void dropOne(Slot slot) {
        drop(slot, false);
    }

    public static void dropStack(Slot slot) {
        drop(slot, true);
    }

    public static void dropStackIfPossible(Slot slot){
        if (slot.hasStack()) dropStack(slot);
    }

    // the second param is special
    public static void drop(Slot slot, boolean ctrl) {
        clickSlot(slot, ctrl ? 1 : 0, SlotActionType.THROW);
    }

    // hotBar: 0-8 or 40
    public static void swapHotBar(Slot slot, int hotBar) {
        clickSlot(slot, hotBar, SlotActionType.SWAP);
    }

    public static void gatherItems(Slot slot) {
        if (isHoldingItem()) {
            clickSlot(slot, 0, SlotActionType.PICKUP_ALL);
        }
    }

    public static void moveToEmpty(Slot slot, Slot empty) {
        leftClick(slot);
        leftClick(empty);
    }

    public static void moveOneItem(Slot to, Slot from) {
        leftClick(from);
        rightClick(to);
        leftClick(from);
    }

    public static void swapSlots(Slot slot, Slot other) {
        leftClick(slot);
        rightClick(other);
        leftClick(slot);
    }

    public static boolean canMergeSlot(Slot to, Slot from) {
        if (to.hasStack() && from.hasStack()) {
            return ItemUtil.canMerge(to.getStack(), from.getStack());
        }
        return true;// empty slots always merge
    }

    public static ItemStack getHeldStack() {
        return getCurrentContainer().getCursorStack();
    }

    public static boolean isHoldingItem() {
        return !getHeldStack().isEmpty();
    }

    public static void putHeldItemDown(ContainerSection section) {
        if (isHoldingItem()) {
            section.getEmptySlot().ifPresent(InventoryUtil::leftClick);
        }
    }

    public static void dropHeldItem() {
        if (isHoldingItem()) {
            clickSlot(-999, 0, SlotActionType.PICKUP);
        }
    }

    public static void quickMove(Slot slot) {
        click(slot, false, SlotActionType.QUICK_MOVE);
    }

    public static void quickMove(int index) {
        click(index, false, SlotActionType.QUICK_MOVE);
    }

    public static void startSpreading(boolean rightClick) {
        clickSlot(-999, ScreenHandler.packQuickCraftData(0, rightClick ? 1 : 0), SlotActionType.QUICK_CRAFT);
    }

    public static void addToSpreading(Slot slot, boolean rightClick) {
        clickSlot(slot, ScreenHandler.packQuickCraftData(1, rightClick ? 1 : 0), SlotActionType.QUICK_CRAFT);
    }

    public static void finishSpreading(boolean rightClick) {
        clickSlot(-999, ScreenHandler.packQuickCraftData(2, rightClick ? 1 : 0), SlotActionType.QUICK_CRAFT);
    }

    public static void leftClick(Slot slot) {
        click(slot, false, SlotActionType.PICKUP);
    }

    public static void leftClick(int index) {
        click(index, false, SlotActionType.PICKUP);
    }

    public static void rightClick(Slot slot) {
        click(slot, true, SlotActionType.PICKUP);
    }

    public static void rightClick(int index) {
        click(index, true, SlotActionType.PICKUP);
    }

    public static void click(Slot slot, boolean rightClick, SlotActionType type) {
        click(getSlotId(slot), rightClick, type);
    }

    public static void click(int index, boolean rightClick, SlotActionType type) {
        clickSlot(index, rightClick ? 1 : 0, type);
    }

    // the button also imply some other data
    public static void clickSlot(Slot slot, int button, SlotActionType type) {
        clickSlot(getSlotId(slot), button, type);
    }

    // This is the final click slot, act as a valve
    public static void clickSlot(int index, int button, SlotActionType type) {
        if (GuiUtils.getCurrentScreen() instanceof CreativeInventoryScreen) {
            ScreenHandler currentContainer = getCurrentContainer();
            currentContainer.onSlotClick(index, button, type, getClientPlayer());
            currentContainer.sendContentUpdates();
        } else {
            getController().clickSlot(getWindowID(), index, button, type, getClientPlayer());
        }
        markDirty();
    }

    private static int changeCount = 0;

    private static void markDirty() {
        changeCount++;
    }

    public static int getChangeCount() {
        return changeCount;
    }

    public static void clickButton(int buttonId) {
        getController().clickButton(getWindowID(), buttonId);
    }

    public static void clickRecipe(RecipeEntry<?> recipe, boolean craftAll) {
        getController().clickRecipe(getWindowID(), recipe, craftAll);
    }

    public static void dropAllMatching(Predicate<ItemStack> predicate) {
        getSlots().stream().filter(x -> predicate.test(x.getStack())).forEach(InventoryUtil::dropStack);
    }

    public static Optional<Slot> getSlotMouseOver() {
        return Optional.ofNullable(AccessorUtil.getHoveredSlot(getGuiContainer()));
    }

    public static int getSlotId(Slot slot) {
        if (slot instanceof CreativeInventoryScreen.CreativeSlot creativeSlot) {
            return creativeSlot.slot.id;
        } else {
            return slot.id;
        }
    }

    public static List<Slot> getSlots() {
        return getSlots(getCurrentContainer());
    }

    public static List<Slot> getSlots(ScreenHandler container) {
        return container.slots;
    }

    public static HandledScreen<?> getGuiContainer() {
        return (HandledScreen<?>) getClient().currentScreen;
    }

    public static int getWindowID() {
        return getCurrentContainer().syncId;
    }

    public static ClientPlayerInteractionManager getController() {
        return getClient().interactionManager;
    }

    public static ScreenHandler getContainer(HandledScreen<?> guiContainer) {
        return guiContainer.getScreenHandler();
    }

    public static ScreenHandler getInventoryContainer() {
        return getClientPlayer().playerScreenHandler;
    }

    public static ScreenHandler getCurrentContainer() {
        return getClientPlayer().currentScreenHandler;
    }

    public static ClientPlayerEntity getClientPlayer() {
        return getClient().player;
    }

    public static MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    public static PlayerInventory getPlayerInventory() {
        return getClientPlayer().getInventory();
    }

    public static boolean isPlayerInventory(Inventory inventory) {
        return inventory == getPlayerInventory();
    }

}
