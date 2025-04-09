package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryTweaks;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.AccessorUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ContainerTemplate {
    @Nullable
    private static List<ItemStack> TEMPLATE = null;

    public static boolean tryRecord(MinecraftClient client) {
        if (Predicates.notInGuiContainer(client)) {
            InfoUtils.printActionbarMessage("记录容器样板: 非容器GUI");
            return false;
        }
        Optional<ContainerSection> optional = InventoryTweaks.getChestSection();
        if (optional.isEmpty()) {
            InfoUtils.printActionbarMessage("记录容器样板: 不支持的容器");
            return false;
        }
        ContainerSection section = optional.get();
        TEMPLATE = section.slots().stream().map(Slot::getStack).map(ItemStack::copy).toList();
        InfoUtils.printActionbarMessage("已记录容器样板" + AccessorUtil.getTypeString(InventoryUtil.getCurrentContainer()));
        return true;
    }

    public static Optional<List<ItemStack>> getTemplate() {
        return Optional.ofNullable(TEMPLATE);
    }
}
