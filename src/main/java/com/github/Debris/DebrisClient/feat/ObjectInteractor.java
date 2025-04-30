package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.util.Predicates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public abstract class ObjectInteractor<T> {
    private final Map<Category, Collection<T>> targets = Util.mapEnum(Category.class, category -> new HashSet<>());

    public boolean hasPending() {
        return targets.values().stream().anyMatch(x -> !x.isEmpty());
    }

    public int size() {
        return targets.values().stream().mapToInt(Collection::size).sum();
    }

    public void clear() {
        targets.values().forEach(Collection::clear);
    }

    public void add(Category category, T object) {
        targets.get(category).add(object);
    }

    public void addAll(Category category, Collection<T> list) {
        targets.get(category).addAll(list);
    }

    protected boolean shouldRemove(T object) {
        return false;
    }

    protected abstract boolean withinReach(MinecraftClient client, T object);

    protected abstract void interact(MinecraftClient client, T object);

    public void onClientTick(MinecraftClient client) {
        if (!hasPending()) return;
        for (Map.Entry<Category, Collection<T>> entry : targets.entrySet()) {
            Category category = entry.getKey();
            if (!category.isActive(client)) continue;

            Collection<T> collection = entry.getValue();
            collection.removeIf(this::shouldRemove);
            if (collection.isEmpty()) continue;

            if (category.isMulti()) {
                collection.forEach(x -> this.interact(client, x));
                collection.clear();
            } else {
                Optional<T> optional = collection.stream().filter(x -> this.withinReach(client, x)).findFirst();
                if (optional.isPresent()) {
                    T object = optional.get();
                    this.interact(client, object);
                    collection.remove(object);
                }
            }
        }
    }

    public enum Category {
        NORMAL,
        OPEN_GUI,
        ;

        private boolean isActive(MinecraftClient client) {
            return switch (this) {
                case NORMAL -> true;
                case OPEN_GUI -> Predicates.inGameNoGui(client);
            };
        }

        private boolean isMulti() {
            return switch (this) {
                case NORMAL -> true;
                case OPEN_GUI -> false;
            };
        }
    }
}
