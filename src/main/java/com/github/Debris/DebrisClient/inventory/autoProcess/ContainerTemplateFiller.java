package com.github.Debris.DebrisClient.inventory.autoProcess;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.feat.ContainerTemplate;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryTweaks;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.inventory.util.ItemUtil;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;
import java.util.Optional;

public class ContainerTemplateFiller implements IAutoProcessor {
    @Override
    public boolean isActive() {
        return DCCommonConfig.AutoContainerTemplateFiller.getBooleanValue();
    }

    @Override
    public ProcessResult process() {
        Optional<ContainerSection> optionalSection = InventoryTweaks.getChestSection();
        if (optionalSection.isEmpty()) {
            InfoUtils.printActionbarMessage("自动样板填充容器: 不支持的容器!");
            return ProcessResult.SKIP;
        }
        Optional<List<ItemStack>> optionalTemplate = ContainerTemplate.getTemplate();
        if (optionalTemplate.isEmpty()) {
            InfoUtils.printActionbarMessage("自动样板填充容器: 请先保存样板!");
            return ProcessResult.SKIP;
        }
        ContainerSection section = optionalSection.get();
        List<ItemStack> template = optionalTemplate.get();
        if (section.size() != template.size()) {
            InfoUtils.printActionbarMessage("自动样板填充容器: 容器尺寸不匹配!");
            return ProcessResult.SKIP;
        }
        if (templateFill(template, section.slots(), EnumSection.InventoryWhole.get())) {
            return ProcessResult.CLOSE_TERMINATE;
        } else {
            return ProcessResult.OPEN_TERMINATE;
        }
    }

    private static boolean templateFill(List<ItemStack> template, List<Slot> to, ContainerSection from) {
        boolean success = true;
        for (int i = 0; i < template.size(); i++) {
            ItemStack itemStack = template.get(i);
            if (itemStack.isEmpty()) continue;// won't supply empty
            Slot slot = to.get(i);
            if (slot.hasStack()) {
                InventoryUtil.leftClick(slot);
                InventoryTweaks.makeSureNotHoldingItem(from);
            }// make it empty
            success &= supplySlot(itemStack, slot, from);
        }
        return success;
    }

    private static boolean supplySlot(ItemStack template, Slot to, ContainerSection from) {
        int countToSupply = template.getCount();
        for (Slot slot : from.slots()) {
            if (!slot.hasStack()) continue;
            ItemStack stack = slot.getStack();
            if (!ItemUtil.compareIDMeta(template, stack)) continue;
            if (stack.getCount() < countToSupply) {// supply part
                countToSupply -= stack.getCount();
                InventoryUtil.moveToEmpty(slot, to);
            } else {// supply all
                InventoryUtil.moveSomeItem(to, slot, countToSupply);
                return true;
            }
        }
        return false;
    }

}
