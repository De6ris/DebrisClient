package com.github.Debris.DebrisClient.inventory.section;

import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.registry.Registries;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.village.MerchantInventory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SectionIdentifier {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Inventory iInventory;

    public SectionIdentifier(Inventory iInventory) {
        this.iInventory = iInventory;
    }

    public void identify(Text title, ScreenHandler container, List<Slot> slotList) {
        try {
            this.identifyInternal(title, container, slotList);
        } catch (Exception e) {
            LOGGER.warn("Error identifying container: ");
            LOGGER.warn("Menu type: {}", container.getType() != null ? Registries.SCREEN_HANDLER.getId(container.getType()) : "<no type>");
            LOGGER.warn("Stack trace: ", e);
            this.handleUnidentified(createSection(slotList));
        }
    }

    private void identifyInternal(Text title, ScreenHandler container, List<Slot> slotList) {
        ContainerSection theWholeSection = createSection(slotList);

        if (InventoryUtil.isPlayerInventory(iInventory)) {
            int size = slotList.size();
            ContainerSection hotBar, playerStorage;
            switch (size) {
                case 9 ->// only hotBar, happens in creative screen
                        putSection(EnumSection.InventoryHotBar, theWholeSection);
                case 36 -> {// only hotBar and storage
                    Slot sample = slotList.getFirst();
                    if (InventoryUtil.getSlotId(sample) == 0) {// this means hotBar then storage
                        hotBar = createSection(slotList.subList(0, 9));
                        playerStorage = createSection(slotList.subList(9, 36));
                    } else {// this means storage then hotBar
                        playerStorage = createSection(slotList.subList(0, 27));
                        hotBar = createSection(slotList.subList(27, 36));
                    }
                    putSection(EnumSection.InventoryHotBar, hotBar);
                    putSection(EnumSection.InventoryStorage, playerStorage);
                }
                case 41 -> {// armor, storage, hotBar, offHand
                    ContainerSection armor = createSection(slotList.subList(0, 4));
                    playerStorage = createSection(slotList.subList(4, 31));
                    hotBar = createSection(slotList.subList(31, 40));
                    ContainerSection offHand = createSection(slotList.subList(40, 41));
                    putSection(EnumSection.InventoryHotBar, hotBar);
                    putSection(EnumSection.InventoryStorage, playerStorage);
                    putSection(EnumSection.Armor, armor);
                    putSection(EnumSection.OffHand, offHand);
                }
            }
            return;
        }

        if (container instanceof AbstractFurnaceScreenHandler) {
            putSection(EnumSection.FurnaceIn, createSection(slotList.subList(0, 1)));
            putSection(EnumSection.FurnaceFuel, createSection(slotList.subList(1, 2)));
            putSection(EnumSection.FurnaceOut, createSection(slotList.subList(2, 3)));
            return;
        }

        if (iInventory instanceof MerchantInventory) {
            putSection(EnumSection.MerchantIn, createSection(slotList.subList(0, 2)));
            putSection(EnumSection.MerchantOut, createSection(slotList.subList(2, 3)));
            return;
        }

        if (container instanceof BrewingStandScreenHandler) {
            putSection(EnumSection.BrewingBottles, createSection(slotList.subList(0, 3)));
            putSection(EnumSection.BrewingIngredient, createSection(slotList.subList(3, 4)));
            putSection(EnumSection.BrewingFuel, createSection(slotList.subList(4, 5)));
            return;
        }

        if (iInventory instanceof RecipeInputInventory) {
            putSection(EnumSection.CraftMatrix, theWholeSection);
            return;
        }

        if (iInventory instanceof CraftingResultInventory) {
            putSection(EnumSection.CraftResult, theWholeSection);
            return;
        }

        if (container instanceof StonecutterScreenHandler) {
            putSection(EnumSection.StoneCutterIn, theWholeSection);
            return;
        }

        if (container instanceof CartographyTableScreenHandler) {
            putSection(EnumSection.CartographyIn, createSection(slotList.subList(0, 1)));
            putSection(EnumSection.CartographyIn2, createSection(slotList.subList(1, 2)));
            return;
        }

        if (container instanceof AnvilScreenHandler) {
            putSection(EnumSection.AnvilIn1, createSection(slotList.subList(0, 1)));
            putSection(EnumSection.AnvilIn2, createSection(slotList.subList(1, 2)));
            return;
        }

        if (container instanceof SmithingScreenHandler) {
            putSection(EnumSection.SmithIn1, createSection(slotList.subList(0, 1)));
            putSection(EnumSection.SmithIn2, createSection(slotList.subList(1, 2)));
            putSection(EnumSection.SmithIn3, createSection(slotList.subList(2, 3)));
            return;
        }

        if (container instanceof GrindstoneScreenHandler) {
            putSection(EnumSection.GrindstoneIn, theWholeSection);
            return;
        }

        if (title.getContent() instanceof TranslatableTextContent translatable) {
            if (translatable.getKey().equals("gca.player.inventory")) {
                putSection(EnumSection.FakePlayerActions, createSection(createFakePlayerActions(slotList)));
                putSection(EnumSection.FakePlayerArmor, createSection(slotList.subList(1, 5)));
                putSection(EnumSection.FakePlayerOffHand, createSection(slotList.subList(7, 8)));
                putSection(EnumSection.FakePlayerInventoryStorage, createSection(slotList.subList(18, 45)));
                putSection(EnumSection.FakePlayerInventoryHotBar, createSection(slotList.subList(45, 54)));
                return;
            }
            if (translatable.getKey().equals("gca.player.ender_chest")) {
                putSection(EnumSection.FakePlayerEnderChestActions, createSection(slotList.subList(0, 27)));
                putSection(EnumSection.FakePlayerEnderChestInventory, createSection(slotList.subList(27, 54)));
                return;
            }
        }

        if (container instanceof CreativeInventoryScreen.CreativeScreenHandler) {
            putSection(EnumSection.CreativeTab, theWholeSection);
            return;
        }

        this.handleUnidentified(theWholeSection);
    }

    private void handleUnidentified(ContainerSection section) {
        SectionHandler.sectionMap.putIfAbsent(EnumSection.Other, section);
        SectionHandler.unIdentifiedSections.add(section);
    }

    private ContainerSection createSection(List<Slot> slots) {
        return new ContainerSection(this.iInventory, slots);
    }

    private void putSection(EnumSection key, List<Slot> slots) {
        putSection(key, createSection(slots));
    }

    private void putSection(EnumSection key, ContainerSection section) {
        if (SectionHandler.sectionMap.containsKey(key)) {
            LOGGER.warn("duplicate section for key {}: {} replacing {}", key, SectionHandler.sectionMap.get(key), section);
        }
        SectionHandler.sectionMap.put(key, section);
    }


    private static List<Slot> createFakePlayerActions(List<Slot> total) {
        List<Slot> slots = new ArrayList<>(total.subList(8, 18));
        slots.add(total.getFirst());
        slots.add(total.get(5));
        slots.add(total.get(6));
        return slots;
    }
}
