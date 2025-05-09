package com.github.debris.debrisclient.compat;

import net.fabricmc.loader.api.FabricLoader;

public class ModReference {
    public static final String CommandButton = "mgbuttons-1_21";
    public static final String Litematica = "litematica";
    public static final String REI = "roughlyenoughitems";
    public static final String MiniHud = "minihud";
    public static final String MagicLibMCApi = "magiclib_minecraft_api";
    public final static String WorldEdit = "worldedit";
    public static final String Tweakeroo = "tweakeroo";
    public static final String ClientCommands = "clientcommands";
    public final static String ClientArguments = "clientarguments";
    public static final String ItemScroller = "itemscroller";
    public static final String LibGui = "libgui";
    public static final String XRay = "advanced-xray-fabric";
    public static final String Wthit = "wthit";

    public static boolean hasMod(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }
}
