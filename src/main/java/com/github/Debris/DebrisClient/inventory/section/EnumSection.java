package com.github.Debris.DebrisClient.inventory.section;

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

    Other,
    ;

    public ContainerSection get() {
        return SectionHandler.getSection(this);
    }

    public boolean isOf(ContainerSection section) {
        return SectionHandler.getSection(this) == section;
    }

}
