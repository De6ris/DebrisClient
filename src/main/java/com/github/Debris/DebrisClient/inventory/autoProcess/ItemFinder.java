package com.github.Debris.DebrisClient.inventory.autoProcess;

import com.github.Debris.DebrisClient.command.DCWhereIsItCommand;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.StringUtil;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemFinder implements IAutoProcessor {
    @Override
    public boolean isActive() {
        return DCWhereIsItCommand.isActive();
    }

    @Override
    public ProcessResult process() {
        List<ContainerSection> sections = SectionHandler.getUnIdentifiedSections();

        if (sections.size() != 1) {
            InfoUtils.printActionbarMessage("物品寻找: 不支持的容器!");
            return ProcessResult.SKIP;
        }

        ContainerSection section = sections.getFirst();
        Set<Item> found = new HashSet<>();
        section.predicateRun(ItemFinder::isTarget, x -> {
            found.add(x.getStack().getItem());
            InventoryUtil.dropStack(x);
        });

        if (!found.isEmpty()) dealFound(found);

        return ProcessResult.CLOSE_TERMINATE;
    }

    private static boolean isTarget(ItemStack itemStack) {
        return DCWhereIsItCommand.streamItems().anyMatch(itemStack::isOf);
    }

    private static void dealFound(Collection<Item> found) {
        DCWhereIsItCommand.markFound(found);
        InfoUtils.printActionbarMessage(String.format("物品寻找: 已找到%s", StringUtil.translateItemCollection(found)));
    }
}
