package com.github.Debris.DebrisClient.mixin.compat.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.logging.HUDController;
import carpet.logging.LoggerRegistry;
import carpet.network.ServerNetworkHandler;
import carpet.script.CarpetScriptServer;
import carpet.script.utils.ParticleParser;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = CarpetServer.class, remap = false)
public class CarpetFix {
    @Shadow
    public static MinecraftServer minecraft_server;

    @Shadow
    public static CarpetScriptServer scriptServer;

    @Shadow
    @Final
    public static List<CarpetExtension> extensions;

    /**
     * @author Debris
     * @reason why you crash me
     */
    @Overwrite
    public static void onServerClosed(MinecraftServer server) {
        if (minecraft_server != null) {
            if (scriptServer != null) {
                scriptServer.onClose();
            }

//            CarpetScriptServer carpetScriptServer = Vanilla.MinecraftServer_getScriptServer(server);
//            if (carpetScriptServer != null && !carpetScriptServer.stopAll) {
//                carpetScriptServer.onClose();
//            }

            scriptServer = null;
            ServerNetworkHandler.close();
            LoggerRegistry.stopLoggers();
            HUDController.resetScarpetHUDs();
            ParticleParser.resetCache();
            extensions.forEach((e) -> {
                e.onServerClosed(server);
            });
            minecraft_server = null;
        }

    }
}
