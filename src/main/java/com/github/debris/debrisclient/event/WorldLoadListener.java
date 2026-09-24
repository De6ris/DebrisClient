package com.github.debris.debrisclient.event;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.advancement.AdventuringTimeHelper;
import com.github.debris.debrisclient.feat.log.GameLogs;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterRecipeStorage;
import com.github.debris.debrisclient.unsafe.MiniHudAccess;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class WorldLoadListener implements IWorldLoadListener {
    private static final WorldLoadListener INSTANCE = new WorldLoadListener();

    public static WorldLoadListener getInstance() {
        return INSTANCE;
    }

    @Override
    public void onWorldLoadPre(ClientLevel worldBefore, ClientLevel worldAfter, Minecraft mc) {
        // Quitting to main menu, save the settings before the integrated server gets shut down
        if (worldBefore != null && worldAfter == null) {
            StoneCutterRecipeStorage.getInstance().write(worldBefore.registryAccess());
            GameLogs.save();
        }
    }

    @Override
    public void onWorldLoadPost(ClientLevel worldBefore, ClientLevel worldAfter, Minecraft mc) {
        if (worldAfter == null) StoneCutterRecipeStorage.getInstance().clearAll();

        // Logging in to a world, load the data
        if (worldBefore == null && worldAfter != null) {
            StoneCutterRecipeStorage.getInstance().read(worldAfter.registryAccess());
            GameLogs.loadOrCreate();
            AdventuringTimeHelper.onWorldLoad(mc);
            if (DCCommonConfig.AlwaysAssumeServux.getBooleanValue() && ModReference.hasMod(ModReference.MiniHud)) {
                MiniHudAccess.setServux(true);
            }
        }

        // Logging out
//        if (worldAfter == null) {
//            ClickPacketBuffer.reset();
//        }
    }
}
