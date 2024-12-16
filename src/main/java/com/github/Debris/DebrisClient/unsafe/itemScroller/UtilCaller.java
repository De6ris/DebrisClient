package com.github.Debris.DebrisClient.unsafe.itemScroller;

import fi.dy.masa.itemscroller.util.InventoryUtils;

public class UtilCaller {
    public static void tryHardTrading(int index) {
        InventoryUtils.villagerTradeEverythingPossibleWithTrade(index);
    }
}
