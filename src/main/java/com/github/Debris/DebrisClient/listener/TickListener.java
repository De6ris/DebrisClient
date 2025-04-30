package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.feat.*;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterUtil;
import com.github.Debris.DebrisClient.render.RenderQueue;
import com.github.Debris.DebrisClient.unsafe.itemScroller.MassCraftingApi;
import com.github.Debris.DebrisClient.util.MiscUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.minecraft.client.MinecraftClient;

public class TickListener implements IClientTickHandler {
    @Override
    public void onClientTick(MinecraftClient client) {
        if (DCCommonConfig.CutStoneThenThrow.getKeybind().isKeybindHeld() || DCCommonConfig.StartStoneCutting.getBooleanValue()) {
            if (StoneCutterUtil.isStoneCutterGui()) {
                StoneCutterUtil.cutStoneThenDrop();
            }
        }

        if (DCCommonConfig.MyMassCrafting.getKeybind().isKeybindHeld() || DCCommonConfig.StartMassCrafting.getBooleanValue()) {
            if (Predicates.hasMod(ModReference.ItemScroller)) {
                if (MassCraftingApi.isCraftingGui()) {
                    MassCraftingApi.tryMassCrafting();
                }
            }
        }

        if (DCCommonConfig.KickBot.getKeybind().isKeybindHeld()) {
            CarpetBot.tryKickBot(client);
        }

        if (DCCommonConfig.AutoThrow.getBooleanValue() && Predicates.inGameNoGui(client)) {
            MiscUtil.runAutoThrow();
        }

        LoyalTrident.onClientTick(client);

        AutoRepeat.onClientTick(client);

        if (DCCommonConfig.AutoExtinguisher.getBooleanValue()) {
            MiscUtil.runAutoExtinguisher(client);
        }

        if (DCCommonConfig.AutoBulletCatching.getBooleanValue()) {
            MiscUtil.runAutoBulletCatcher(client);
        }

        BlockInteractor.INSTANCE.onClientTick(client);

        EntityInteractor.INSTANCE.onClientTick(client);

        RenderQueue.onClientTick(client);
    }


    public static void onRenderTick(MinecraftClient client) {
    }

}
