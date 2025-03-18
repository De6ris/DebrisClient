package com.github.Debris.DebrisClient.thread;

import com.github.Debris.DebrisClient.util.AccessorUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public final class TakeOffTask implements Runnable {
    public static volatile boolean Running = false;

    private final MinecraftClient client;
    private final ClientPlayerEntity player;
    private final Runnable swapTask;

    public TakeOffTask(MinecraftClient client, ClientPlayerEntity player, Runnable swapTask) {
        this.client = client;
        this.player = player;
        this.swapTask = swapTask;
    }

    @Override
    public void run() {
        while (Predicates.inGameNoGui(client)) {
            if (player.checkGliding()) {
                player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                AccessorUtil.use(client);
                swapTask.run();
                Running = false;
                return;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
