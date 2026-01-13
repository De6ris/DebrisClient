package com.github.debris.debrisclient.compat;

import com.github.debris.debrisclient.util.Platform;

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
    public static final String BetterPvP = "xaerobetterpvp";
    public static final String XaeroMiniMap = "xaerominimap";
    public static final String JourneyMap = "journeymap";

    public static boolean hasMod(String modid) {
        return Platform.hasMod(modid);
    }
}
