package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterUtil;
import com.github.Debris.DebrisClient.unsafe.itemScroller.MassCraftingApi;
import com.github.Debris.DebrisClient.util.*;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class TickListener implements IClientTickHandler {
    @Override
    public void onClientTick(MinecraftClient client) {
        if (DCCommonConfig.CutStoneThenThrow.getKeybind().isKeybindHeld() || DCCommonConfig.StartStoneCutting.getBooleanValue()) {
            if (StoneCutterUtil.isStoneCutterGui()) {
                StoneCutterUtil.cutStoneThenDrop();
            }
        }

        if (DCCommonConfig.MyMassCrafting.getKeybind().isKeybindHeld() || DCCommonConfig.StartMassCrafting.getBooleanValue()) {
            if (FabricLoader.getInstance().isModLoaded(ModReference.ItemScroller)) {
                if (MassCraftingApi.isCraftingGui()) {
                    MassCraftingApi.tryMassCrafting();
                }
            }
        }

        if (DCCommonConfig.KickBot.getKeybind().isKeybindHeld()) {
            BotUtil.tryKickBot(client);
        }

        if (DCCommonConfig.AutoThrow.getBooleanValue() && Predicates.inGameNoGui(client)) {
            MiscUtil.runAutoThrow();
        }

        TridentUtil.onClientTick(client);

        AutoRepeat.onClientTick(client);

        if (DCCommonConfig.AutoExtinguisher.getBooleanValue()) {
            MiscUtil.runAutoExtinguisher(client);
        }
    }


    public static void onRenderTick(MinecraftClient client) {
        if (DCCommonConfig.AutoGuiQuitting.getBooleanValue()) {
            if (client.currentScreen instanceof HandledScreen<?> guiContainer) {
                guiContainer.close();
            }
        }
    }

}
