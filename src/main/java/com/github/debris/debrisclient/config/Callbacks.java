package com.github.debris.debrisclient.config;

import com.github.debris.debrisclient.feat.*;
import com.github.debris.debrisclient.feat.interactor.InteractionFactory;
import com.github.debris.debrisclient.gui.InventoryConfigScreen;
import com.github.debris.debrisclient.gui.MainConfigScreen;
import com.github.debris.debrisclient.gui.UniversalSearchScreen;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterRecipeStorage;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterUtil;
import com.github.debris.debrisclient.inventory.feat.InventoryTweaks;
import com.github.debris.debrisclient.inventory.feat.SyncContainer;
import com.github.debris.debrisclient.inventory.sort.SortInventory;
import com.github.debris.debrisclient.util.Predicates;
import net.minecraft.client.Minecraft;

public class Callbacks {
    public static void init(Minecraft client) {
        DCCommonConfig.OpenConfigScreen.getKeybind().setCallback((action, key) -> {
            client.setScreenAndShow(MainConfigScreen.getInstance(null));
            return true;
        });

        DCCommonConfig.OpenInventoryConfigScreen.getKeybind().setCallback((action, key) -> {
            client.setScreenAndShow(InventoryConfigScreen.getInstance(null));
            return true;
        });

        DCCommonConfig.OpenUniversalSearch.getKeybind().setCallback((action, key) -> {
            client.setScreenAndShow(new UniversalSearchScreen());
            return true;
        });


        DCCommonConfig.RestoreKicking.getKeybind().setCallback((action, key) -> CarpetBot.restoreKicking(client));

        DCCommonConfig.SuggestBotSpawnCommand.getKeybind().setCallback((action, key) -> CarpetBot.suggestBotSpawnCommand(client));

        DCCommonConfig.ResendLastChat.getKeybind().setCallback((action, key) -> ResendChat.resendLast(client));

        DCCommonConfig.RepeatNewestChat.getKeybind().setCallback((action, key) -> ResendChat.repeatNewestChat(client));

        DCCommonConfig.AlignWithEnderEye.getKeybind().setCallback((action, key) -> MiscFeat.alignWithEnderEye(client));

        DCCommonConfig.TakeOff.getKeybind().setCallback((action, key) -> TakeOff.tryTakeOff(client));

        DCCommonConfig.OpenSelectionContainers.getKeybind().setCallback(((action, key) -> InteractionFactory.addBlockTask(client, InteractionFactory.BlockPredicate.CONTAINER, true)));

        DCCommonConfig.InteractSelectionEntities.getKeybind().setCallback(((action, key) -> InteractionFactory.addEntityTask(client, true)));

        DCCommonConfig.TEST.getKeybind().setCallback((action, key) -> {
            return false;
        });

        DCCommonConfig.AutoRepeatBlackList.setValueChangeCallback(config -> AutoRepeat.updateBlackList(config.getStrings()));

        initInventory(client);
    }

    private static void initInventory(Minecraft client) {
        InventoryConfig.SwitchPreset.setValueChangeCallback(InventoryPreset::switchPreset);

        InventoryConfig.SortInventory.getKeybind().setCallback((action, key) -> SortInventory.onKey(client));

        InventoryConfig.StoreStoneCutterRecipe.getKeybind().setCallback((action, key) -> {
            if (StoneCutterUtil.isStoneCutterRecipeViewOpen() && StoneCutterUtil.isOverStoneCutterResult()) {
                StoneCutterRecipeStorage.getInstance().storeRecipe();
                return true;
            }
            return false;
        });

        InventoryConfig.CutStone.getKeybind().setCallback((action, key) -> {
            if (StoneCutterUtil.isStoneCutterGui()) {
                StoneCutterUtil.cutStone();
                return true;
            }
            return false;
        });

        InventoryConfig.ThrowSection.getKeybind().setCallback((action, key) -> {
            if (Predicates.notInGuiContainer(client)) return false;
            return InventoryTweaks.tryThrowSection();
        });

        InventoryConfig.ThrowSame.getKeybind().setCallback((action, key) -> {
            if (Predicates.notInGuiContainer(client)) return false;
            return InventoryTweaks.tryDropSame();
        });

        InventoryConfig.SpawnBotForItem.getKeybind().setCallback((action, key) -> CarpetBot.spawnBotOfItem(client));

        InventoryConfig.SyncContainer.getKeybind().setCallback((action, key) -> SyncContainer.trySync(client));
    }

}
