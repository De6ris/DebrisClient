package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.inventory.section.ContainerSection;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

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

    public static void dropStackIfPossible(Slot slot) {
        if (slot.hasItem()) dropStack(slot);
    }

    // the second param is special
    public static void drop(Slot slot, boolean ctrl) {
        clickSlot(slot, ctrl ? 1 : 0, ClickType.THROW);
    }

    // hotBar: 0-8 or 40
    public static void swapHotBar(Slot slot, int hotBar) {
        clickSlot(slot, hotBar, ClickType.SWAP);
    }

    public static void gatherItems(Slot slot) {
        if (isHoldingItem()) {
            clickSlot(slot, 0, ClickType.PICKUP_ALL);
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

    public static void moveSomeItem(Slot to, Slot from, int count) {
        if (count <= 0) return;
        leftClick(from);
        for (int i = 0; i < count; i++) {
            rightClick(to);
        }
        leftClick(from);
    }

    // 40 is off-hand
    public static void swapSlots(Slot slot, Slot other) {
        swapHotBar(slot, 40);
        swapHotBar(other, 40);
        swapHotBar(slot, 40);
    }

    public static boolean canMergeSlot(Slot to, Slot from) {
        if (to.hasItem() && from.hasItem()) {
            return ItemUtil.canMerge(to.getItem(), from.getItem());
        }
        return true;// empty slots always merge
    }

    public static ItemStack getHeldStack() {
        return getCurrentContainer().getCarried();
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
            clickSlot(-999, 0, ClickType.PICKUP);
        }
    }

    public static void quickMove(Slot slot) {
        click(slot, false, ClickType.QUICK_MOVE);
    }

    public static void quickMove(int index) {
        click(index, false, ClickType.QUICK_MOVE);
    }

    public static void startSpreading(boolean rightClick) {
        clickSlot(-999, AbstractContainerMenu.getQuickcraftMask(0, rightClick ? 1 : 0), ClickType.QUICK_CRAFT);
    }

    public static void addToSpreading(Slot slot, boolean rightClick) {
        clickSlot(slot, AbstractContainerMenu.getQuickcraftMask(1, rightClick ? 1 : 0), ClickType.QUICK_CRAFT);
    }

    public static void finishSpreading(boolean rightClick) {
        clickSlot(-999, AbstractContainerMenu.getQuickcraftMask(2, rightClick ? 1 : 0), ClickType.QUICK_CRAFT);
    }

    public static void leftClick(Slot slot) {
        click(slot, false, ClickType.PICKUP);
    }

    public static void leftClick(int index) {
        click(index, false, ClickType.PICKUP);
    }

    public static void rightClick(Slot slot) {
        click(slot, true, ClickType.PICKUP);
    }

    public static void rightClick(int index) {
        click(index, true, ClickType.PICKUP);
    }

    public static void click(Slot slot, boolean rightClick, ClickType type) {
        click(getSlotId(slot), rightClick, type);
    }

    public static void click(int index, boolean rightClick, ClickType type) {
        clickSlot(index, rightClick ? 1 : 0, type);
    }

    // the button also imply some other data
    public static void clickSlot(Slot slot, int button, ClickType type) {
        clickSlot(getSlotId(slot), button, type);
    }

    // This is the final click slot, act as a valve
    public static void clickSlot(int index, int button, ClickType type) {
        if (GuiUtils.getCurrentScreen() instanceof CreativeModeInventoryScreen) {
            AbstractContainerMenu currentContainer = getCurrentContainer();
            currentContainer.clicked(index, button, type, getClientPlayer());
            currentContainer.broadcastChanges();
        } else {
            getController().handleInventoryMouseClick(getWindowID(), index, button, type, getClientPlayer());
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
        getController().handleInventoryButtonClick(getWindowID(), buttonId);
    }

    public static void clickRecipe(RecipeDisplayId recipe, boolean craftAll) {
        getController().handlePlaceRecipe(getWindowID(), recipe, craftAll);
    }

    public static void dropAllMatching(Predicate<ItemStack> predicate) {
        getSlots().stream().filter(x -> predicate.test(x.getItem())).forEach(InventoryUtil::dropStack);
    }

    public static Optional<Slot> getSlotMouseOver() {
        return Optional.ofNullable(AccessorUtil.getHoveredSlot(getGuiContainer()));
    }

    public static int getSlotId(Slot slot) {
        if (slot instanceof CreativeModeInventoryScreen.SlotWrapper creativeSlot) {
            return creativeSlot.target.index;
        } else {
            return slot.index;
        }
    }

    public static List<Slot> getSlots() {
        return getSlots(getCurrentContainer());
    }

    public static List<Slot> getSlots(AbstractContainerMenu container) {
        return container.slots;
    }

    public static AbstractContainerScreen<?> getGuiContainer() {
        return (AbstractContainerScreen<?>) getClient().screen;
    }

    public static int getWindowID() {
        return getCurrentContainer().containerId;
    }

    public static MultiPlayerGameMode getController() {
        return getClient().gameMode;
    }

    public static AbstractContainerMenu getContainer(AbstractContainerScreen<?> guiContainer) {
        return guiContainer.getMenu();
    }

    public static AbstractContainerMenu getInventoryContainer() {
        return getClientPlayer().inventoryMenu;
    }

    public static AbstractContainerMenu getCurrentContainer() {
        return getClientPlayer().containerMenu;
    }

    public static LocalPlayer getClientPlayer() {
        return getClient().player;
    }

    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }

    public static Inventory getPlayerInventory() {
        return getClientPlayer().getInventory();
    }

    public static boolean isPlayerInventory(Container inventory) {
        return inventory instanceof Inventory;
    }

    @SuppressWarnings("DataFlowIssue")
    public static String getTypeString(AbstractContainerMenu container) {
        MenuType<?> menuType = AccessorUtil.getMenuType(container);
        return menuType != null ? BuiltInRegistries.MENU.getKey(menuType).toString() : "<no type>";
    }

}
