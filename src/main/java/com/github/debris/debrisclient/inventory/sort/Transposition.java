package com.github.debris.debrisclient.inventory.sort;

import java.util.function.BiConsumer;

public record Transposition(int first, int second) {
    public Transposition {
        if (first < 0 || second < 0) throw new IllegalArgumentException();
    }

    public <T> void swap(T[] array) {
        T temp = array[second];
        array[second] = array[first];
        array[first] = temp;
    }

    public void swap(int[] data) {
        int temp = data[second];
        data[second] = data[first];
        data[first] = temp;
    }

    public <T> void operate(T[] array, BiConsumer<T, T> operation) {
        operation.accept(array[first], array[second]);
    }
}
