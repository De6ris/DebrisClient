package com.github.Debris.DebrisClient.inventory.stoneCutter;

import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
        this.input = EnumSection.StoneCutterIn.get().slots().getFirst().getStack().copy();
        this.result = EnumSection.CraftResult.get().slots().getFirst().getStack().copy();
        this.searchVanillaRecipe();
    }

    public boolean isValid() {
        return !this.input.isEmpty() && !this.result.isEmpty();
    }

    private void searchVanillaRecipe() {
    }

    public void readFromNBT(NbtCompound nbt, DynamicRegistryManager registryManager) {
        if (nbt.contains("Result", NbtElement.COMPOUND_TYPE) && nbt.contains("Input", NbtElement.COMPOUND_TYPE)) {
            this.input = ItemStack.fromNbtOrEmpty(registryManager, nbt.getCompound("Input"));
            this.result = ItemStack.fromNbtOrEmpty(registryManager, nbt.getCompound("Result"));
        }
    }

    // Assuming Valid
    public NbtCompound writeToNBT(DynamicRegistryManager registryManager) {
        NbtCompound nbt = new NbtCompound();
        NbtCompound inputNbt = (NbtCompound) this.input.encode(registryManager);
        NbtCompound resultNbt = (NbtCompound) this.result.encode(registryManager);
        nbt.put("Input", inputNbt);
        nbt.put("Result", resultNbt);
        return nbt;
    }
}
