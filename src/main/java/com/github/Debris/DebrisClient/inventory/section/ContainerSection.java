package com.github.Debris.DebrisClient.inventory.section;

import com.github.Debris.DebrisClient.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.ItemUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record ContainerSection(List<Slot> slots) {
    public static final ContainerSection EMPTY = new ContainerSection(List.of());

    public static boolean isActionSection(ContainerSection section) {
        for (EnumSection enumSection : EnumSection.ACTIONS) {
            if (section.isOf(enumSection)) return true;
        }
        return false;
    }

    public boolean hasSlot(Slot slot) {
        return this.slots.contains(slot);
    }

    public int getLocalIndex(Slot slot) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (getSlot(i) == slot) return i;
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
        return InventoryUtil.getSlotId(getSlot(localIndex));
    }

    public int toLocalIndex(int globalIndex) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (InventoryUtil.getSlotId(getSlot(i)) == globalIndex) return i;
        }
        return 0;
    }

    public boolean isEmpty() {
        return this.slots.stream().noneMatch(Slot::hasStack);
    }

    public boolean isFull() {
        return this.slots.stream().allMatch(Slot::hasStack);
    }

    public int size() {
        return this.slots.size();
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

    public boolean hasItem(Item item) {
        return this.slots.stream().anyMatch(x -> x.hasStack() && x.getStack().isOf(item));
    }

    public Optional<Slot> findItem(ItemStack itemStack) {
        return this.slots.stream().filter(x -> x.hasStack() && ItemUtil.compareIDMeta(x.getStack(), itemStack))
                .max(Comparator.comparingInt(slot -> slot.getStack().getCount()));
    }

    public Optional<Slot> findItem(Item item) {
        return this.slots.stream().filter(x -> x.hasStack() && x.getStack().isOf(item))
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
        for (int i = this.slots.size() - 1; i >= 1; i--) {// inverse order reduce operations; skip first slot
            Slot slot = getSlot(i);
            if (!slot.hasStack()) continue;// skip those empty
            mergeSlotToPrevious(i, slot);
        }
    }

    private void mergeSlotToPrevious(int currentIndex, Slot currentSlot) {
        InventoryUtil.leftClick(currentSlot);// pick up
        for (int i = 0; i < currentIndex; i++) {
            Slot slot = getSlot(i);
            if (slot.hasStack() && ItemUtil.canMerge(slot.getStack(), InventoryUtil.getHeldStack())) {
                InventoryUtil.leftClick(slot);
                if (!InventoryUtil.isHoldingItem()) {
                    return;
                }
            }
        }
        if (InventoryUtil.isHoldingItem()) InventoryUtil.putHeldItemDown(this);
    }

    public void fillBlanks() {
        for (int i = this.slots.size() - 1; i >= 0; i--) {// inverse order reduce operations
            Slot slot = getSlot(i);
            if (i == 0) continue;// just skip the first slot
            if (!slot.hasStack()) continue;// skip those empty
            this.moveToPreviousEmpty(i, slot);
        }
    }

    private void moveToPreviousEmpty(int currentIndex, Slot currentSlot) {
        for (int i = 0; i < currentIndex; i++) {
            Slot slot = getSlot(i);
            if (!slot.hasStack()) {
                InventoryUtil.moveToEmpty(currentSlot, slot);
//                ManyLib.logger.info("moving {} to empty {}", currentIndex, i);
                return;
            }
        }
    }

    public Stream<Slot> stream() {
        return this.slots.stream();
    }

    public Stream<Slot> streamEmpty() {
        return this.stream().filter(x -> !x.hasStack());
    }

    public Stream<Slot> streamNotEmpty() {
        return this.stream().filter(Slot::hasStack);
    }

    /**
     * Note that the empty slots are skipped.
     */
    public Stream<Slot> predicate(Predicate<ItemStack> predicate) {
        return this.streamNotEmpty().filter(slot -> predicate.test(slot.getStack()));
    }

    public void allRun(Consumer<Slot> runnable) {
        for (Slot slot : this.slots) {
            runnable.accept(slot);
        }
    }

    public void emptyRun(Consumer<Slot> runnable) {
        for (Slot slot : this.slots) {
            if (slot.hasStack()) continue;
            runnable.accept(slot);
        }
    }

    public void notEmptyRun(Consumer<Slot> runnable) {
        for (Slot slot : this.slots) {
            if (slot.hasStack()) runnable.accept(slot);
        }
    }

    /**
     * Note that the empty slots are skipped.
     */
    public void predicateRun(Predicate<ItemStack> predicate, Consumer<Slot> runnable) {
        for (Slot slot : this.slots) {
            if (slot.hasStack() && predicate.test(slot.getStack())) runnable.accept(slot);
        }
    }

    public ContainerSection mergeWith(ContainerSection other) {
        ImmutableList.Builder<Slot> builder = ImmutableList.builder();
        builder.addAll(this.slots);
        builder.addAll(other.slots);
        return new ContainerSection(builder.build());
    }

    public boolean isOf(EnumSection section) {
        if (SectionHandler.hasSection(section)) {
            return SectionHandler.getSection(section) == this;
        }
        return false;
    }

    public ContainerSection subSection(int fromIndex, int toIndex) {
        return new ContainerSection(this.slots.subList(fromIndex, toIndex));
    }

    @Override
    public String toString() {
        return "ContainerSection[slots=" + slots.toString() + "]";
    }
}
