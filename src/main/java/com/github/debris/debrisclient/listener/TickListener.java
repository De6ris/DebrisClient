package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.AutoRepeat;
import com.github.debris.debrisclient.feat.CarpetBot;
import com.github.debris.debrisclient.feat.CommandQueue;
import com.github.debris.debrisclient.feat.MiscFeat;
import com.github.debris.debrisclient.feat.interactor.BlockInteractor;
import com.github.debris.debrisclient.feat.interactor.EntityInteractor;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterUtil;
import com.github.debris.debrisclient.inventory.feat.AutoThrow;
import com.github.debris.debrisclient.inventory.feat.LoyalTrident;
import com.github.debris.debrisclient.render.RenderQueue;
import com.github.debris.debrisclient.unsafe.itemScroller.MassCraftingApi;
import com.github.debris.debrisclient.util.Predicates;
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
            if (ModReference.hasMod(ModReference.ItemScroller)) {
                if (MassCraftingApi.isCraftingGui()) {
                    MassCraftingApi.tryMassCrafting();
                }
            }
        }

        if (DCCommonConfig.KickBot.getKeybind().isKeybindHeld()) {
            CarpetBot.tryKickBot(client);
        }

        if (DCCommonConfig.AutoThrow.getBooleanValue() && Predicates.inGameNoGui(client)) {
            AutoThrow.runAutoThrow();
        }

        LoyalTrident.onClientTick(client);

        AutoRepeat.onClientTick(client);

        if (DCCommonConfig.AutoExtinguisher.getBooleanValue()) {
            MiscFeat.runAutoExtinguisher(client);
        }

        if (DCCommonConfig.AutoBulletCatching.getBooleanValue()) {
            MiscFeat.runAutoBulletCatcher(client);
        }

        BlockInteractor.INSTANCE.onClientTick(client);

        EntityInteractor.INSTANCE.onClientTick(client);

        RenderQueue.onClientTick(client);

        CommandQueue.onClientTick(client);
    }


    public static void onRenderTick(MinecraftClient client) {
    }

}
