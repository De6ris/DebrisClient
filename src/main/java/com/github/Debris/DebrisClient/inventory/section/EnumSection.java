package com.github.Debris.DebrisClient.inventory.section;

import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public enum EnumSection {
    Armor,
    InventoryStorage,
    InventoryHotBar,

    InventoryWhole {
        @Override
        public ContainerSection get() {
            return InventoryStorage.get().mergeWith(InventoryHotBar.get());
        }
    },

    OffHand,

    CreativeTab,

    CraftMatrix,// this is widely used, crafter(grandpa cow), crafting, player crafting
    CraftResult,// this is widely used, cartography, crafter(grandpa cow), crafting, forging, grindstone, player crafting, stone cutter

    FurnaceIn,
    FurnaceOut,
    FurnaceFuel,

    MerchantIn,
    MerchantOut,

    BrewingBottles,
    BrewingIngredient,
    BrewingFuel,

    StoneCutterIn,

    CartographyIn,
    CartographyIn2,

    AnvilIn1,
    AnvilIn2,

    SmithIn1,
    SmithIn2,
    SmithIn3,

    GrindstoneIn,

    FakePlayerActions,
    FakePlayerArmor,
    FakePlayerInventoryStorage,
    FakePlayerInventoryHotBar,
    FakePlayerOffHand,

    FakePlayerEnderChestActions,
    FakePlayerEnderChestInventory,

    Unidentified,// Generally, it is a simple section like chest or shulker box, or absent in special containers that already identified in SectionIdentifier.

    Container {// Those slots in current container that are not for player inventory.
        @Override
        public ContainerSection get() {
            ScreenHandler container = InventoryUtil.getCurrentContainer();
            PlayerInventory playerInventory = InventoryUtil.getPlayerInventory();
            List<Slot> containerSlots = InventoryUtil.getSlots(container).stream().filter(x -> x.inventory != playerInventory).toList();
            if (containerSlots.isEmpty()) {
                return ContainerSection.EMPTY;
            }
            return new ContainerSection(containerSlots);
        }
    },
    ;

    /**
     * Generally absent and returns {@link ContainerSection#EMPTY}. Only call this when your container corresponds.
     * <br>
     * Those overrides this method do not exist in the {@link SectionHandler#sectionMap}, this is very important when you stream all the container sections in
     * {@link SectionHandler#streamAllSections()} or {@link SectionHandler#getSection(Slot)} and so on.
     */
    public ContainerSection get() {
        return SectionHandler.getSection(this);
    }

}
