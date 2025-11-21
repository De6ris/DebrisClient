package com.github.debris.debrisclient.inventory.cutstone;

import com.github.debris.debrisclient.inventory.section.EnumSection;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;

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
        this.input = EnumSection.StoneCutterIn.get().getFirstSlot().getItem().copy();
        this.result = EnumSection.CraftResult.get().getFirstSlot().getItem().copy();
        this.searchVanillaRecipe();
    }

    public boolean isValid() {
        return !this.input.isEmpty() && !this.result.isEmpty();
    }

    private void searchVanillaRecipe() {
    }

    public void readFromNBT(CompoundTag nbt, RegistryAccess registryManager) {
        if (nbt.contains("Result") && nbt.contains("Input")) {
            this.input = InventoryUtils.fromNbtOrEmpty(registryManager, nbt.getCompoundOrEmpty("Input"));
            this.result = InventoryUtils.fromNbtOrEmpty(registryManager, nbt.getCompoundOrEmpty("Result"));
        }
    }

    // Assuming Valid
    public CompoundTag writeToNBT(RegistryAccess registryManager) {
        CompoundTag nbt = new CompoundTag();
        CompoundTag inputNbt = (CompoundTag) ItemStack.CODEC.encodeStart(registryManager.createSerializationContext(NbtOps.INSTANCE), this.input).getPartialOrThrow();
        CompoundTag resultNbt = (CompoundTag) ItemStack.CODEC.encodeStart(registryManager.createSerializationContext(NbtOps.INSTANCE), this.result).getPartialOrThrow();
        nbt.put("Input", inputNbt);
        nbt.put("Result", resultNbt);
        return nbt;
    }
}
