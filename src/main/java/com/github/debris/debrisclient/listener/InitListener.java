package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.config.Callbacks;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.gui.DCConfigUi;
import com.github.debris.debrisclient.gui.UniversalSearchScreen;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ModInfo;
import net.minecraft.client.Minecraft;

public class InitListener implements IInitializationHandler {
    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(DebrisClient.MOD_NAME, DCCommonConfig.getInstance());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputListener.getInstance());
        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputListener.getInstance());
        InputEventHandler.getInputManager().registerMouseInputHandler(InputListener.getInstance());
        Callbacks.init(Minecraft.getInstance());
        TickHandler.getInstance().registerClientTickHandler(new TickListener());
        WorldLoadHandler.getInstance().registerWorldLoadPreHandler(WorldLoadListener.getInstance());
        WorldLoadHandler.getInstance().registerWorldLoadPostHandler(WorldLoadListener.getInstance());
        RenderEventHandler.getInstance().registerWorldLastRenderer(RenderListener.getInstance());
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(new ModInfo(DebrisClient.MOD_ID, DebrisClient.MOD_NAME, DCConfigUi::new));
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(UniversalSearchScreen.Instance);
    }
}
