package com.github.debris.debrisclient.inventory.sort;

import java.util.List;
import java.util.function.BiConsumer;

public interface Permutation {
    ProductPermutation EMPTY = new ProductPermutation(List.of());

    boolean isEmpty();

    <T> void map(T[] array);

    void map(int[] data);

    <T> void operate(T[] array, BiConsumer<T, T> operation);
}
