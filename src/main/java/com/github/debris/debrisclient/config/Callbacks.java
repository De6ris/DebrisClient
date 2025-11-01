package com.github.debris.debrisclient.config;

import com.github.debris.debrisclient.feat.*;
import com.github.debris.debrisclient.feat.advancement.AdventuringTimeHelper;
import com.github.debris.debrisclient.feat.interactor.InteractionFactory;
import com.github.debris.debrisclient.gui.DCConfigUi;
import com.github.debris.debrisclient.gui.UniversalSearchScreen;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterRecipeStorage;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterUtil;
import com.github.debris.debrisclient.inventory.feat.InventoryTweaks;
import com.github.debris.debrisclient.inventory.feat.SyncContainer;
import com.github.debris.debrisclient.inventory.sort.SortInventory;
import com.github.debris.debrisclient.util.Predicates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class Callbacks {
    public static void init(MinecraftClient client) {
        DCCommonConfig.OpenWindow.getKeybind().setCallback((action, key) -> {
            client.setScreen(new DCConfigUi());
            return true;
        });

        DCCommonConfig.OpenUniversalSearch.getKeybind().setCallback((action, key) -> {
            client.setScreen(new UniversalSearchScreen());
            return true;
        });

        DCCommonConfig.SortItem.getKeybind().setCallback((action, key) -> {
            if (Predicates.notInGuiContainer(client)) return false;
            playClickSound(client);
            return SortInventory.trySort();// this will block other click consumers
        });

        DCCommonConfig.StoreStoneCutterRecipe.getKeybind().setCallback((action, key) -> {
            if (StoneCutterUtil.isStoneCutterRecipeViewOpen() && StoneCutterUtil.isOverStoneCutterResult()) {
                StoneCutterRecipeStorage.getInstance().storeRecipe();
                return true;
            }
            return false;
        });

        DCCommonConfig.CutStone.getKeybind().setCallback((action, key) -> {
            if (StoneCutterUtil.isStoneCutterGui()) {
                StoneCutterUtil.cutStone();
                return true;
            }
            return false;
        });

        DCCommonConfig.ThrowSection.getKeybind().setCallback((action, key) -> {
            if (Predicates.notInGuiContainer(client)) return false;
            return InventoryTweaks.tryThrowSection();
        });

        DCCommonConfig.ThrowSimilar.getKeybind().setCallback((action, key) -> {
            if (Predicates.notInGuiContainer(client)) return false;
            return InventoryTweaks.tryDropSimilar();
        });

        DCCommonConfig.RestoreKicking.getKeybind().setCallback((action, key) -> CarpetBot.restoreKicking(client));

        DCCommonConfig.BotSpawnCommand.getKeybind().setCallback((action, key) -> CarpetBot.suggestBotSpawnCommand(client));

        DCCommonConfig.ResendLastChat.getKeybind().setCallback((action, key) -> ResendChat.resendLast(client));

        DCCommonConfig.RepeatNewestChat.getKeybind().setCallback((action, key) -> ResendChat.repeatNewestChat(client));

        DCCommonConfig.AlignWithEnderEye.getKeybind().setCallback((action, key) -> MiscFeat.alignWithEnderEye(client));

        DCCommonConfig.TakeOff.getKeybind().setCallback((action, key) -> TakeOff.tryTakeOff(client));

        DCCommonConfig.SyncContainer.getKeybind().setCallback((action, key) -> SyncContainer.trySync(client));

        DCCommonConfig.OpenSelectionContainers.getKeybind().setCallback(((action, key) -> InteractionFactory.addBlockTask(client, InteractionFactory.BlockPredicate.CONTAINER, true)));

        DCCommonConfig.InteractSelectionEntities.getKeybind().setCallback(((action, key) -> InteractionFactory.addEntityTask(client, true)));

        DCCommonConfig.TEST.getKeybind().setCallback((action, key) -> {
            return false;
        });

        DCCommonConfig.AutoRepeatBlackList.setValueChangeCallback(config -> AutoRepeat.updateBlackList(config.getStrings()));

        DCCommonConfig.AdventuringTimeHelper.setValueChangeCallback(config -> AdventuringTimeHelper.onConfigChange(client));

    }

    private static void playClickSound(MinecraftClient client) {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
