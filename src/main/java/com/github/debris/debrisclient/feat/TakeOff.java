package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.EnumSection;
import com.github.debris.debrisclient.util.AccessorUtil;
import com.github.debris.debrisclient.util.InteractionUtil;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.Predicates;
import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class TakeOff {
    public static boolean Running = false;

    @SuppressWarnings("ConstantConditions")
    public static boolean tryTakeOff(Minecraft client) {
        if (!Predicates.inGameNoGui(client)) return false;

        LocalPlayer player = client.player;
        if (player.getAbilities().flying) return false;

        ContainerSection section = EnumSection.OffHand.get().mergeWith(EnumSection.InventoryWhole.get());

        if (!wearElytra(section)) return false;

        Runnable swapTask;

        if (player.getMainHandItem().is(Items.FIREWORK_ROCKET)) {
            swapTask = Runnables.doNothing();
        } else {
            Optional<Slot> optional = section.findItem(Items.FIREWORK_ROCKET);
            if (optional.isEmpty()) return false;
            Slot slot = optional.get();
            int currentHotBar = InteractionUtil.getCurrentHotBar(client);
            swapTask = () -> InventoryUtil.swapHotBar(slot, currentHotBar);
        }

        swapTask.run();// take out rocket

        if (player.isFallFlying()) {
            AccessorUtil.use(client);
            swapTask.run();// restore
            return true;
        }

        if (Running) return false;
        Running = true;

        player.jumpFromGround();

        FutureTaskQueue.addTask(new TakeOffTask(swapTask));

        return true;
    }

    private static boolean wearElytra(ContainerSection section) {
        Item elytra = Items.ELYTRA;
        Slot chestSlot = EnumSection.Armor.get().getSlot(1);
        if (chestSlot.getItem().is(elytra)) return true;
        Optional<Slot> optional = section.findItem(elytra);
        if (optional.isPresent()) {
            InventoryUtil.swapSlots(chestSlot, optional.get());
            return true;
        }
        return false;
    }

    private record TakeOffTask(Runnable swapTask) implements FutureTask {
        @Override
        public boolean execute(Minecraft client) {
            if (!Predicates.inGameNoGui(client)) return true;
            LocalPlayer player = client.player;
            if (player == null) return true;
            if (player.tryToStartFallFlying()) {
                player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                AccessorUtil.use(client);
                swapTask.run();
                Running = false;
                return true;
            }
            return false;
        }
    }
}
