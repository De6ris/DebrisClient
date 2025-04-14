package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.inventory.autoProcess.IAutoProcessor;
import com.github.Debris.DebrisClient.inventory.autoProcess.ProcessResult;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryTweaks;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.inventory.util.ItemUtil;
import com.github.Debris.DebrisClient.util.AccessorUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import com.github.Debris.DebrisClient.util.RayTraceUtil;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ContainerTemplate {
    private static boolean RUNNING = false;
    @Nullable
    private static List<ItemStack> TEMPLATE = null;

    public static boolean tryRecord(MinecraftClient client) {
        if (RUNNING) {
            InfoUtils.printActionbarMessage("记录容器样板: 等待容器打开中");
            return false;
        }

        if (TEMPLATE != null) {
            TEMPLATE = null;
            InfoUtils.printActionbarMessage("记录容器样板: 已重置");
            return true;
        }

        boolean waitForInteractor = false;

        if (Predicates.notInGuiContainer(client)) {
            Optional<HitResult> optional = RayTraceUtil.getPlayerRayTrace(client);
            if (optional.isPresent() && isContainerBlock(client, optional.get())) {
                BlockInteractor.add(((BlockHitResult) optional.get()).getBlockPos());
                waitForInteractor = true;
            } else {
                InfoUtils.printActionbarMessage("记录容器样板: 未看向容器");
                return false;
            }
        }

        if (waitForInteractor) {
            RUNNING = true;
            InfoUtils.printActionbarMessage("记录容器样板: 等待容器打开");
        } else {
            recordInternal();
            InventoryUtil.getGuiContainer().close();
        }

        return true;
    }

    private static void recordInternal() {
        TEMPLATE = EnumSection.Container.get().slots().stream().map(Slot::getStack).map(ItemStack::copy).toList();
        InfoUtils.printActionbarMessage("已记录容器样板" + AccessorUtil.getTypeString(InventoryUtil.getCurrentContainer()));
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean isContainerBlock(MinecraftClient client, HitResult trace) {
        if (trace.getType() != HitResult.Type.BLOCK) return false;
        BlockPos blockPos = ((BlockHitResult) trace).getBlockPos();
        return Predicates.isContainerBlock(client.world, blockPos);
    }

    public static class Recorder implements IAutoProcessor {
        @Override
        public boolean isActive() {
            return RUNNING;
        }

        @Override
        public ProcessResult process() {
            recordInternal();
            RUNNING = false;
            return ProcessResult.CLOSE_TERMINATE;
        }
    }

    public static class Filler implements IAutoProcessor {
        @Override
        public boolean isActive() {
            return TEMPLATE != null;
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        public ProcessResult process() {
            List<ItemStack> template = TEMPLATE;
            ContainerSection section = EnumSection.Container.get();
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
                Slot slot = to.get(i);
                if (slot.hasStack()) {
                    InventoryUtil.leftClick(slot);
                    InventoryTweaks.makeSureNotHoldingItem(from);
                }// make it empty
                ItemStack itemStack = template.get(i);
                if (itemStack.isEmpty()) continue;// won't supply empty
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
}
