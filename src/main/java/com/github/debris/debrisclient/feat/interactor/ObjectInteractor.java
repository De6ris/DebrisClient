package com.github.debris.debrisclient.feat.interactor;

import com.github.debris.debrisclient.util.Predicates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ObjectInteractor<T> {
    /**
     * Use list here to support multi interactions for one single object.
     */
    private final Map<Category, List<T>> targets = Util.mapEnum(Category.class, category -> new ArrayList<>());

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

    /**
     * @return If true, task success and remove.
     */
    protected abstract boolean interact(MinecraftClient client, T object);

    public void onClientTick(MinecraftClient client) {
        if (!hasPending()) return;
        for (Map.Entry<Category, List<T>> pair : targets.entrySet()) {
            Category category = pair.getKey();
            if (!category.isActive(client)) continue;

            List<T> list = pair.getValue();
            list.removeIf(this::shouldRemove);
            if (list.isEmpty()) continue;

            Stream<T> inReachStream = list.stream().filter(x -> this.withinReach(client, x));
            Map<T, Long> toInteract = makeInteractMap(category, inReachStream);
            List<T> successList = getSuccessList(client, toInteract);
            list.removeAll(successList);
        }
    }

    private Map<T, Long> makeInteractMap(Category category, Stream<T> stream) {
        if (category.isMulti()) {
            return stream.collect(Collectors.groupingBy(
                    Function.identity(),
                    Collectors.counting()
            ));
        } else {
            return stream.findFirst().map(t -> Map.of(t, 1L)).orElseGet(Map::of);
        }
    }

    private List<T> getSuccessList(MinecraftClient client, Map<T, Long> toInteract) {
        List<T> successList = new ArrayList<>();

        for (Map.Entry<T, Long> entry : toInteract.entrySet()) {
            T object = entry.getKey();
            Long times = entry.getValue();
            boolean success = false;
            for (int i = 0; i < times; i++) {
                success |= this.interact(client, object);
            }
            if (success) successList.add(object);
        }
        return successList;
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
