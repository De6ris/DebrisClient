package com.github.Debris.DebrisClient.inventory.autoProcess;

import com.github.Debris.DebrisClient.command.DCWhereIsItCommand;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.StringUtil;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
            found.add(x.getStack().getItem());
            InventoryUtil.dropStack(x);
        });

        if (!found.isEmpty()) {
            dealFound(found);
        } else {
            if (AutoProcessManager.allowMessage()) {
                InfoUtils.printActionbarMessage("debris_client.auto_processor.item_finder.not_found");
            }
        }

        return ProcessResult.CLOSE_TERMINATE;
    }

    private static boolean isTarget(ItemStack itemStack) {
        return DCWhereIsItCommand.streamItems().anyMatch(itemStack::isOf);
    }

    private static void dealFound(Collection<Item> found) {
        DCWhereIsItCommand.markFound(found);
        InfoUtils.printActionbarMessage("debris_client.auto_processor.item_finder.found", StringUtil.translateItemCollection(found));
    }
}
