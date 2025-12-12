package com.github.debris.debrisclient.inventory.autoprocess;

import com.github.debris.debrisclient.command.DCWhereIsItCommand;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.localization.AutoProcessText;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.StringUtil;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ItemFinder implements IAutoProcessor {
    @Override
    public boolean isActive() {
        return DCWhereIsItCommand.isActive();
    }

    @Override
    public ProcessResult process(ContainerSection containerSection, ContainerSection playerInventory) {
        Set<Item> found = new HashSet<>();
        containerSection.predicateRun(ItemFinder::isTarget, x -> {
            found.add(x.getItem().getItem());
            InventoryUtil.dropStack(x);
        });

        if (!found.isEmpty()) {
            dealFound(found);
        } else {
            if (AutoProcessManager.allowMessage()) {
                InfoUtils.sendVanillaMessage(AutoProcessText.ITEM_FINDER_NOT_FOUND.translate());
            }
        }

        return ProcessResult.CLOSE_TERMINATE;
    }

    private static boolean isTarget(ItemStack itemStack) {
        return DCWhereIsItCommand.streamItems().anyMatch(itemStack::is);
    }

    private static void dealFound(Collection<Item> found) {
        DCWhereIsItCommand.markFound(found);
        InfoUtils.sendVanillaMessage(AutoProcessText.ITEM_FINDER_FOUND.translate(StringUtil.translateItemCollection(found)));
    }
}
