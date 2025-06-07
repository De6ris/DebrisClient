package com.github.debris.debrisclient.feat.interactor;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.client.MinecraftClient;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ObjectInteractor<T> {
    private final Set<T> targets = new HashSet<>();

    private int ticksToWait = 0;

    public boolean hasPending() {
        return !targets.isEmpty();
    }

    public int size() {
        return targets.size();
    }

    public void clear() {
        targets.clear();
    }

    public boolean clearAndInform() {
        if (hasPending()) {
            clear();
            informClear();
            return true;
        }
        return false;
    }

    protected void informClear() {
    }

    public void add(T object) {
        targets.add(object);
    }

    public void addAll(Collection<T> list) {
        targets.addAll(list);
    }

    protected boolean shouldRemove(T object) {
        return false;
    }

    protected abstract boolean withinReach(MinecraftClient client, T object);

    protected abstract InteractResult interact(MinecraftClient client, T object);

    public void onClientTick(MinecraftClient client) {
        if (ticksToWait > 0) {
            ticksToWait -= 1;
            return;
        }
        if (!hasPending()) return;

        Set<T> set = targets;

        set.removeIf(this::shouldRemove);
        if (set.isEmpty()) return;

        Set<T> successEntries = new HashSet<>();

        Set<T> inReachEntries = set.stream().filter(x -> this.withinReach(client, x)).collect(Collectors.toSet());

        loop:
        for (T t : inReachEntries) {
            InteractResult result = this.interact(client, t);
            switch (result) {
                case SUCCESS -> successEntries.add(t);
                case FAIL -> {
                }
                case WAITING -> {
                    ticksToWait = DCCommonConfig.InteractContainerPeriod.getIntegerValue();
                    successEntries.add(t);
                    break loop;
                }
            }
        }

        set.removeAll(successEntries);
    }
}
