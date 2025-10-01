package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.EnumSection;
import com.github.debris.debrisclient.util.AccessorUtil;
import com.github.debris.debrisclient.util.InteractionUtil;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.Predicates;
import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;

public class TakeOff {
    public static boolean Running = false;

    @SuppressWarnings("ConstantConditions")
    public static boolean tryTakeOff(MinecraftClient client) {
        if (!Predicates.inGameNoGui(client)) return false;

        ClientPlayerEntity player = client.player;
        if (player.getAbilities().flying) return false;

        ContainerSection section = EnumSection.OffHand.get().mergeWith(EnumSection.InventoryWhole.get());

        if (!wearElytra(section)) return false;

        Runnable swapTask;

        if (player.getMainHandStack().isOf(Items.FIREWORK_ROCKET)) {
            swapTask = Runnables.doNothing();
        } else {
            Optional<Slot> optional = section.findItem(Items.FIREWORK_ROCKET);
            if (optional.isEmpty()) return false;
            Slot slot = optional.get();
            int currentHotBar = InteractionUtil.getCurrentHotBar(client);
            swapTask = () -> InventoryUtil.swapHotBar(slot, currentHotBar);
        }

        swapTask.run();// take out rocket

        if (player.isGliding()) {
            AccessorUtil.use(client);
            swapTask.run();// restore
            return true;
        }

        if (Running) return false;
        Running = true;

        player.jump();

        FutureTaskQueue.addTask(new TakeOffTask(swapTask));

        return true;
    }

    private static boolean wearElytra(ContainerSection section) {
        Item elytra = Items.ELYTRA;
        Slot chestSlot = EnumSection.Armor.get().getSlot(1);
        if (chestSlot.getStack().isOf(elytra)) return true;
        Optional<Slot> optional = section.findItem(elytra);
        if (optional.isPresent()) {
            InventoryUtil.swapSlots(chestSlot, optional.get());
            return true;
        }
        return false;
    }

    private record TakeOffTask(Runnable swapTask) implements FutureTask {
        @Override
        public boolean execute(MinecraftClient client) {
            if (!Predicates.inGameNoGui(client)) return true;
            ClientPlayerEntity player = client.player;
            if (player == null) return true;
            if (player.checkGliding()) {
                player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                AccessorUtil.use(client);
                swapTask.run();
                Running = false;
                return true;
            }
            return false;
        }
    }
}
