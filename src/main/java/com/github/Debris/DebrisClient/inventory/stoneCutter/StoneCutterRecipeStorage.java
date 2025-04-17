package com.github.Debris.DebrisClient.inventory.stoneCutter;

import com.github.Debris.DebrisClient.DebrisClient;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.DynamicRegistryManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class StoneCutterRecipeStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_PAGES = 1;
    private static final int MAX_RECIPES = 18;
    private static final StoneCutterRecipeStorage INSTANCE = new StoneCutterRecipeStorage(MAX_PAGES * MAX_RECIPES);

    private final StoneCutterRecipePattern[] recipes;

    private int selected;
    private boolean dirty;

    public static StoneCutterRecipeStorage getInstance() {
        return INSTANCE;
    }

    private StoneCutterRecipeStorage(int size) {
        this.recipes = new StoneCutterRecipePattern[size];
        this.initArray();
    }

    private void initArray() {
        for (int i = 0; i < this.recipes.length; i++) {
            this.recipes[i] = new StoneCutterRecipePattern();
        }
    }

    public int getSelection() {
        return this.selected;
    }

    public void setCurrentSelected(int index) {
        if (index >= 0 && index < this.recipes.length) {
            this.selected = index;
            this.dirty = true;
        }
    }

    public void scrollSelection(boolean forward) {
        this.setCurrentSelected(this.selected + (forward ? 1 : -1));
    }

    public int getFirstVisibleRecipeId() {
        return this.getCurrentRecipePage() * this.getRecipeCountPerPage();
    }

    public int getTotalRecipeCount() {
        return this.recipes.length;
    }

    public int getRecipeCountPerPage() {
        return MAX_RECIPES;
    }

    public int getCurrentRecipePage() {
        return this.getSelection() / this.getRecipeCountPerPage();
    }

    public void clearAll() {
        for (StoneCutterRecipePattern recipe : this.recipes) {
            recipe.clear();
        }
        this.markDirty();
    }

    public StoneCutterRecipePattern getRecipe(int index) {
        if (index >= 0 && index < this.recipes.length) {
            return this.recipes[index];
        }
        return this.recipes[0];
    }

    public StoneCutterRecipePattern getSelectedRecipe() {
        return this.getRecipe(this.getSelection());
    }

    public void storeRecipe() {
        this.getRecipe(this.getSelection()).storeRecipe();
        this.markDirty();
    }

    private void markDirty() {
        this.dirty = true;
    }

    private File getSaveDir() {
        return new File(DebrisClient.CONFIG_DIR + "stone_cutter_recipes");
    }

    private String getFileName() {
        String worldName = StringUtils.getWorldOrServerName();

        if (worldName != null) {
            return "recipes_" + worldName + ".nbt";
        } else {
            return "recipes_unknown.nbt";
        }
    }

    public void read(DynamicRegistryManager registryManager) {
        try {
            File saveDir = this.getSaveDir();

            File file = new File(saveDir, this.getFileName());

            if (file.exists()) {
                if (file.isFile() && file.canRead()) {
                    this.initArray();

                    FileInputStream is = new FileInputStream(file);
                    this.readFromNBT(NbtIo.readCompressed(is, NbtSizeTracker.ofUnlimitedBytes()), registryManager);
                    is.close();
                } else {
                    LOGGER.warn("readFromDisk(): Error reading recipes from file '{}'", file.getPath());
                }
            }
        } catch (Exception e) {
            LOGGER.warn("readFromDisk(): Failed to read recipes from file", e);
        }
    }

    private void readFromNBT(NbtCompound nbt, DynamicRegistryManager registryManager) {
        if (nbt == null || nbt.contains("Recipes") == false) {
            return;
        }

        NbtList tagList = nbt.getListOrEmpty("Recipes");
        int count = tagList.size();

        for (int i = 0; i < count; i++) {
            NbtCompound tag = tagList.getCompoundOrEmpty(i);

            int index = tag.getByte("RecipeIndex", (byte) -1);

            if (index >= 0 && index < this.recipes.length) {
                this.recipes[index].readFromNBT(tag, registryManager);
            }
        }

        this.setCurrentSelected(nbt.getByte("Selected", (byte) 0));
    }

    public void write(DynamicRegistryManager registryManager) {
        if (this.dirty) {
            try {
                File saveDir = this.getSaveDir();

                if (saveDir.exists() == false) {
                    if (saveDir.mkdirs() == false) {
                        LOGGER.error("writeToDisk(): Failed to create the recipe storage directory '{}'", saveDir.getPath());
                        return;
                    }
                }

                File fileTmp = new File(saveDir, this.getFileName() + ".tmp");
                File fileReal = new File(saveDir, this.getFileName());
                FileOutputStream os = new FileOutputStream(fileTmp);
                NbtIo.writeCompressed(this.writeToNBT(registryManager), os);
                os.close();

                if (fileReal.exists()) {
                    if (fileReal.delete() == false) {
                        LOGGER.warn("writeToDisk(): failed to delete file {} ", fileReal.getName());
                    }
                }

                if (fileTmp.renameTo(fileReal) == false) {
                    LOGGER.warn("writeToDisk(): failed to rename file {} ", fileTmp.getName());
                }
                this.dirty = false;
            } catch (Exception e) {
                LOGGER.warn("writeToDisk(): Failed to write recipes to file!", e);
            }
        }
    }

    private NbtCompound writeToNBT(DynamicRegistryManager registryManager) {
        NbtList tagRecipes = new NbtList();
        NbtCompound nbt = new NbtCompound();

        for (int i = 0; i < this.recipes.length; i++) {
            if (this.recipes[i].isValid()) {
                StoneCutterRecipePattern entry = this.recipes[i];
                NbtCompound tag = entry.writeToNBT(registryManager);
                tag.putByte("RecipeIndex", (byte) i);
                tagRecipes.add(tag);
            }
        }

        nbt.put("Recipes", tagRecipes);
        nbt.putByte("Selected", (byte) this.selected);

        return nbt;
    }
}
