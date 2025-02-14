package com.github.Debris.DebrisClient.inventory.section;


import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.inventory.util.ItemUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record ContainerSection(Inventory inventory, List<Slot> slots) {
    public boolean isInventoryHotBar() {
        return InventoryUtil.isPlayerInventory(this.inventory) && this.slots.size() == 9;
    }

    public boolean isInventoryStorage() {
        return InventoryUtil.isPlayerInventory(this.inventory) && this.slots.size() == 27;
    }

    public boolean hasSlot(Slot slot) {
        return this.slots.contains(slot);
    }

    public int getLocalIndex(Slot slot) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i) == slot) return i;
        }
        return 0;
    }

    public Slot getFirstSlot() {
        return this.slots.getFirst();
    }

    public Slot getSlot(int index) {
        return this.slots.get(index);
    }

    public void leftClick(int localIndex) {
        InventoryUtil.leftClick(this.toGlobalIndex(localIndex));
    }

    public void leftClick(Slot slot) {
        InventoryUtil.leftClick(slot);
    }

    public int toGlobalIndex(int localIndex) {
        return InventoryUtil.getSlotId(this.slots.get(localIndex));
    }

    public int toLocalIndex(int globalIndex) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (InventoryUtil.getSlotId(this.slots.get(i)) == globalIndex) return i;
        }
        return 0;
    }

    public boolean isEmpty() {
        return this.slots.stream().noneMatch(Slot::hasStack);
    }

    public boolean isFull() {
        return this.slots.stream().allMatch(Slot::hasStack);
    }

    public Optional<Slot> getEmptySlot() {
        return this.slots.stream().filter(x -> !x.hasStack()).findFirst();
    }

    public boolean moveToEmpty(Slot slot) {
        Optional<Slot> emptySlot = this.getEmptySlot();
        if (emptySlot.isPresent()) {
            InventoryUtil.moveToEmpty(slot, emptySlot.get());
            return true;
        } else {
            return false;
        }
    }

    public void moveToSection(int localIndex, ContainerSection other) {
        other.getEmptySlot().ifPresent(x -> {
            this.leftClick(localIndex);
            other.leftClick(x);
        });
    }

    public void moveToSection(Slot slot, ContainerSection other) {
        other.getEmptySlot().ifPresent(x -> {
            this.leftClick(slot);
            other.leftClick(x);
        });
    }

    public Optional<Slot> hasItem(ItemStack itemStack) {
        return this.slots.stream().filter(x -> x.hasStack() && ItemUtil.compareIDMeta(x.getStack(), itemStack))
                .max(Comparator.comparingInt(slot -> slot.getStack().getCount()));
    }

    public Optional<Slot> providesOneScroll(ItemStack itemStack) {
        return this.slots.stream().filter(x -> x.hasStack() && ItemUtil.canMerge(itemStack, x.getStack())).findFirst();
    }

    public Optional<Slot> absorbsOneScroll(ItemStack itemStack) {
        return this.slots.stream().filter(x -> x.hasStack() && ItemUtil.canMerge(x.getStack(), itemStack)).findFirst();
    }

    public boolean acceptsItem(ItemStack itemStack) {
        return this.slots.stream().anyMatch(x -> !x.hasStack() || (x.hasStack() && ItemUtil.canMerge(x.getStack(), itemStack)));
    }

    public void mergeSlots() {
        for (int i = this.slots.size() - 1; i >= 0; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (i == 0) continue;// just skip the first slot
            if (!slot.hasStack()) continue;// skip those empty
            mergeSlotToPrevious(i, slot);
        }
    }

    private void mergeSlotToPrevious(int currentIndex, Slot currentSlot) {
        for (int i = 0; i < currentIndex; i++) {
            Slot slot = this.slots.get(i);
            if (slot.hasStack() && InventoryUtil.canMergeSlot(slot, currentSlot)) {
                InventoryUtil.leftClick(currentSlot);
                InventoryUtil.leftClick(slot);
                InventoryUtil.putHeldItemDown(this);
//                ManyLib.logger.info("merging {} to {}", currentIndex, i);
                return;
            }
        }
    }

    public void fillBlanks() {
        for (int i = this.slots.size() - 1; i >= 0; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (i == 0) continue;// just skip the first slot
            if (!slot.hasStack()) continue;// skip those empty
            this.moveToPreviousEmpty(i, slot);
        }
    }

    private void moveToPreviousEmpty(int currentIndex, Slot currentSlot) {
        for (int i = 0; i < currentIndex; i++) {
            Slot slot = this.slots.get(i);
            if (!slot.hasStack()) {
                InventoryUtil.moveToEmpty(currentSlot, slot);
//                ManyLib.logger.info("moving {} to empty {}", currentIndex, i);
                return;
            }
        }
    }

    public void predicateRun(Predicate<ItemStack> predicate, Consumer<Slot> runnable) {
        for (Slot slot : this.slots) {
            if (slot.hasStack() && predicate.test(slot.getStack())) {
                runnable.accept(slot);
            }
        }
    }

    public void notEmptyRun(Consumer<Slot> runnable) {
        for (Slot slot : this.slots) {
            if (slot.hasStack()) {
                runnable.accept(slot);
            }
        }
    }

    public void emptyRun(Consumer<Slot> runnable) {
        for (Slot slot : this.slots) {
            if (slot.hasStack()) continue;
            runnable.accept(slot);
        }
    }

    public void allRun(Consumer<Slot> runnable) {
        for (Slot slot : this.slots) {
            runnable.accept(slot);
        }
    }

    public ContainerSection mergeWith(ContainerSection other) {
        if (this.inventory != other.inventory) {
            throw new IllegalArgumentException();
        }
        ImmutableList.Builder<Slot> builder = ImmutableList.builder();
        builder.addAll(this.slots);
        builder.addAll(other.slots);
        return new ContainerSection(this.inventory, builder.build());
    }

    public boolean isOf(EnumSection section) {
        return SectionHandler.getSection(section) == this;
    }

    public ContainerSection subSection(int fromIndex, int toIndex) {
        return new ContainerSection(this.inventory, this.slots.subList(fromIndex, toIndex));
    }

    @Override
    public String toString() {
        return "ContainerSection[inventory=" + inventory.toString() + ", slots=" + slots.toString() + "]";
    }
}
