package com.github.debris.debrisclient.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;

public class InventoryPreset {
    public static void switchPreset(ConfigBoolean config) {
        if (config.getBooleanValue()) {
            InventoryConfig.SortInventory.getKeybind().setValueFromString("R");
            InventoryConfig.ModifierSpreadItem.getKeybind().setValueFromString("LEFT_ALT");
            InventoryConfig.ModifierMoveSame.getKeybind().setValueFromString("LEFT_CONTROL");
            InventoryConfig.ModifierMoveStack.getKeybind().setValueFromString("LEFT_SHIFT");
            InventoryConfig.ModifierMoveAll.getKeybind().setValueFromString("SPACE");
            InventoryConfig.ModifierClearBundle.getKeybind().setValueFromString("LEFT_SHIFT");
            InventoryConfig.MyMassCrafting.getKeybind().setValueFromString("LEFT_CONTROL,C");
            InventoryConfig.ThrowSection.getKeybind().setValueFromString("SPACE,Q");
            InventoryConfig.ThrowSame.getKeybind().setValueFromString("LEFT_SHIFT,Q");
            InventoryConfig.SpawnBotForItem.getKeybind().setValueFromString("Y");
        } else {
            InventoryConfig.SortInventory.resetToDefault();
            InventoryConfig.ModifierSpreadItem.resetToDefault();
            InventoryConfig.ModifierMoveSame.resetToDefault();
            InventoryConfig.ModifierMoveStack.resetToDefault();
            InventoryConfig.ModifierMoveAll.resetToDefault();
            InventoryConfig.ModifierClearBundle.resetToDefault();
            InventoryConfig.MyMassCrafting.resetToDefault();
            InventoryConfig.ThrowSection.resetToDefault();
            InventoryConfig.ThrowSame.resetToDefault();
            InventoryConfig.SpawnBotForItem.resetToDefault();
        }
    }
}
