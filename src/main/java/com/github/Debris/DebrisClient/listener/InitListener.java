package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.DebrisClient;
import com.github.Debris.DebrisClient.config.Callbacks;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.minecraft.client.MinecraftClient;

public class InitListener implements IInitializationHandler {
    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(DebrisClient.MOD_NAME, DCCommonConfig.getInstance());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputListener.getInstance());
        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputListener.getInstance());
        InputEventHandler.getInputManager().registerMouseInputHandler(InputListener.getInstance());
        Callbacks.init(MinecraftClient.getInstance());
        TickHandler.getInstance().registerClientTickHandler(new TickListener());
        WorldLoadHandler.getInstance().registerWorldLoadPreHandler(WorldLoadListener.getInstance());
        WorldLoadHandler.getInstance().registerWorldLoadPostHandler(WorldLoadListener.getInstance());
        RenderEventHandler.getInstance().registerWorldLastRenderer(RenderListener.getInstance());
    }
}
