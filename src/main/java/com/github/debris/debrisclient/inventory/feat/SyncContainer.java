package com.github.debris.debrisclient.inventory.feat;

import com.github.debris.debrisclient.feat.interactor.BlockInteractor;
import com.github.debris.debrisclient.feat.interactor.InteractionFactory;
import com.github.debris.debrisclient.inventory.autoprocess.AutoProcessManager;
import com.github.debris.debrisclient.inventory.autoprocess.IAutoProcessor;
import com.github.debris.debrisclient.inventory.autoprocess.ProcessResult;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.EnumSection;
import com.github.debris.debrisclient.localization.AutoProcessText;
import com.github.debris.debrisclient.localization.SyncContainerText;
import com.github.debris.debrisclient.util.BlockUtil;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.ItemUtil;
import com.github.debris.debrisclient.util.RayTraceUtil;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SyncContainer {
    private static boolean WAITING_TEMPLATE = false;
    private static Block TYPE = Blocks.AIR;
    @Nullable
    private static List<ItemStack> TEMPLATE = null;

    @SuppressWarnings("DataFlowIssue")
    public static boolean trySync(Minecraft client) {
        if (BlockInteractor.INSTANCE.clearAndInform()) return true;

        if (WAITING_TEMPLATE) {
            InfoUtils.sendVanillaMessage(SyncContainerText.WAIT_CONTAINER_OPEN.translate());
            return false;
        }

        Screen screen = GuiUtils.getCurrentScreen();
        if (screen != null) screen.onClose();

        Optional<BlockPos> optional = RayTraceUtil.getRayTraceBlock(client);
        if (optional.isPresent() && BlockUtil.isContainer(client.level, optional.get())) {
            BlockPos blockPos = optional.get();
            BlockInteractor.INSTANCE.add(blockPos);
            TYPE = client.level.getBlockState(blockPos).getBlock();
        } else {
            InfoUtils.sendVanillaMessage(SyncContainerText.NO_CONTAINER_PRESENT.translate());
            return false;
        }

        WAITING_TEMPLATE = true;
        InfoUtils.sendVanillaMessage(SyncContainerText.WAIT_CONTAINER_OPEN.translate());

        return true;
    }

    private static void syncInternal() {
        TEMPLATE = EnumSection.Container.get().slots().stream().map(Slot::getItem).map(ItemStack::copy).toList();
        InfoUtils.sendVanillaMessage(AutoProcessText.TEMPLATE_RECORDER_MESSAGE.translate(InventoryUtil.getGuiContainer().getTitle()));
        InteractionFactory.addBlockTask(Minecraft.getInstance(), (world, pos) -> world.getBlockState(pos).is(TYPE), false);
    }

    public static class Recorder implements IAutoProcessor {
        @Override
        public boolean isActive() {
            return WAITING_TEMPLATE;
        }

        @Override
        public ProcessResult process(ContainerSection containerSection, ContainerSection playerInventory) {
            syncInternal();
            WAITING_TEMPLATE = false;
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
        public ProcessResult process(ContainerSection containerSection, ContainerSection playerInventory) {
            List<ItemStack> template = TEMPLATE;

            if (!BlockInteractor.INSTANCE.hasPending()) TEMPLATE = null;// disable the filler

            ContainerSection section = EnumSection.Container.get();
            if (section.size() != template.size()) {
                InfoUtils.sendVanillaMessage(AutoProcessText.TEMPLATE_FILLER_SIZE_UNMATCH.translate(InventoryUtil.getGuiContainer().getTitle()));
                return ProcessResult.SKIP;
            }
            if (templateFill(template, section.slots(), EnumSection.InventoryWhole.get())) {
                if (AutoProcessManager.allowMessage()) {
                    InfoUtils.sendVanillaMessage(AutoProcessText.TEMPLATE_FILLER_SUCCESS.translate(InventoryUtil.getGuiContainer().getTitle()));
                }
                return ProcessResult.CLOSE_TERMINATE;
            } else {
                return ProcessResult.OPEN_TERMINATE;
            }
        }

        private static boolean templateFill(List<ItemStack> template, List<Slot> to, ContainerSection from) {
            boolean success = true;
            for (int i = 0; i < template.size(); i++) {
                Slot slot = to.get(i);
                if (slot.hasItem()) {
                    InventoryUtil.leftClick(slot);
                    InventoryTweaks.clearCursor(from);
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
                if (!slot.hasItem()) continue;
                ItemStack stack = slot.getItem();
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
