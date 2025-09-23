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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SyncContainer {
    private static boolean WAITING_TEMPLATE = false;
    private static Block TYPE = Blocks.AIR;
    @Nullable
    private static List<ItemStack> TEMPLATE = null;

    @SuppressWarnings("DataFlowIssue")
    public static boolean trySync(MinecraftClient client) {
        if (BlockInteractor.INSTANCE.clearAndInform()) return true;

        if (WAITING_TEMPLATE) {
            InfoUtils.sendVanillaMessage(SyncContainerText.WAIT_CONTAINER_OPEN.text());
            return false;
        }

        Screen screen = GuiUtils.getCurrentScreen();
        if (screen != null) screen.close();

        Optional<BlockPos> optional = RayTraceUtil.getRayTraceBlock(client);
        if (optional.isPresent() && BlockUtil.isContainer(client.world, optional.get())) {
            BlockPos blockPos = optional.get();
            BlockInteractor.INSTANCE.add(blockPos);
            TYPE = client.world.getBlockState(blockPos).getBlock();
        } else {
            InfoUtils.sendVanillaMessage(SyncContainerText.NO_CONTAINER_PRESENT.text());
            return false;
        }

        WAITING_TEMPLATE = true;
        InfoUtils.sendVanillaMessage(SyncContainerText.WAIT_CONTAINER_OPEN.text());

        return true;
    }

    private static void syncInternal() {
        TEMPLATE = EnumSection.Container.get().slots().stream().map(Slot::getStack).map(ItemStack::copy).toList();
        InfoUtils.sendVanillaMessage(AutoProcessText.TEMPLATE_RECORDER_MESSAGE.text(InventoryUtil.getGuiContainer().getTitle()));
        InteractionFactory.addBlockTask(MinecraftClient.getInstance(), (world, pos) -> world.getBlockState(pos).isOf(TYPE), false);
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
                InfoUtils.sendVanillaMessage(AutoProcessText.TEMPLATE_FILLER_SIZE_UNMATCH.text(InventoryUtil.getGuiContainer().getTitle()));
                return ProcessResult.SKIP;
            }
            if (templateFill(template, section.slots(), EnumSection.InventoryWhole.get())) {
                if (AutoProcessManager.allowMessage()) {
                    InfoUtils.sendVanillaMessage(AutoProcessText.TEMPLATE_FILLER_SUCCESS.text(InventoryUtil.getGuiContainer().getTitle()));
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
                if (slot.hasStack()) {
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
