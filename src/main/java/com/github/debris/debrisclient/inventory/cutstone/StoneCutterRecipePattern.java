package com.github.debris.debrisclient.inventory.cutstone;

import com.github.debris.debrisclient.inventory.section.EnumSection;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;

public class StoneCutterRecipePattern {
    private ItemStack input = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;

    public ItemStack getInput() {
        return this.input;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void clear() {
        this.input = ItemStack.EMPTY;
        this.result = ItemStack.EMPTY;
    }

    public void storeRecipe() {
        this.input = EnumSection.StoneCutterIn.get().getFirstSlot().getStack().copy();
        this.result = EnumSection.CraftResult.get().getFirstSlot().getStack().copy();
        this.searchVanillaRecipe();
    }

    public boolean isValid() {
        return !this.input.isEmpty() && !this.result.isEmpty();
    }

    private void searchVanillaRecipe() {
    }

    public void readFromNBT(NbtCompound nbt, DynamicRegistryManager registryManager) {
        if (nbt.contains("Result") && nbt.contains("Input")) {
            this.input = InventoryUtils.fromNbtOrEmpty(registryManager, nbt.getCompoundOrEmpty("Input"));
            this.result = InventoryUtils.fromNbtOrEmpty(registryManager, nbt.getCompoundOrEmpty("Result"));
        }
    }

    // Assuming Valid
    public NbtCompound writeToNBT(DynamicRegistryManager registryManager) {
        NbtCompound nbt = new NbtCompound();
        NbtCompound inputNbt = (NbtCompound) ItemStack.CODEC.encodeStart(registryManager.getOps(NbtOps.INSTANCE), this.input).getPartialOrThrow();
        NbtCompound resultNbt = (NbtCompound) ItemStack.CODEC.encodeStart(registryManager.getOps(NbtOps.INSTANCE), this.result).getPartialOrThrow();
        nbt.put("Input", inputNbt);
        nbt.put("Result", resultNbt);
        return nbt;
    }
}
