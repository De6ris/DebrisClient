package com.github.Debris.DebrisClient.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class PermutationUtil {
    public static <T> List<Transposition> getOptimalProcess(T[] original, Comparator<T> comparator) {
        List<Transposition> rawTransposition = getRawTransposition(original, comparator);
        return optimize(rawTransposition, original.length - 1);
    }

    private static <T> List<Transposition> getRawTransposition(T[] original, Comparator<T> comparator) {
        T[] clone = original.clone();
        int length = clone.length;
        if (length <= 1) return List.of();
        List<Transposition> list = new ArrayList<>();
        for (int i = 1; i < length; i++) {
            boolean flag = true;
            for (int j = 0; j < length - i; j++) {
                T elementJ = clone[j];
                T elementJ_1 = clone[j + 1];
                int compare = comparator.compare(elementJ, elementJ_1);
                if (compare > 0) {
                    clone[j] = elementJ_1;
                    clone[j + 1] = elementJ;
                    list.add(new Transposition(j, j + 1));
                    flag = false;
                }
            }
            if (flag) break;
        }
        return list;
    }

    // bound: the biggest possible integer in transpositions
    private static List<Transposition> optimize(List<Transposition> original, int bound) {
        int[] data = new int[bound + 1];
        for (int i = 0; i < data.length; i++) {
            data[i] = i;
        }// make it as 0,1,...,bound
        for (Transposition transposition : original) {
            transposition.swap(data);
        }
        return getMinimalTransposition(data);
    }

    /*
     * We receive a shuffled array, and return a minimal transposition list.
     * This list tell us how to shuffle(order to random), so we sort it(random to order) and reverse(order to random).
     * */
    private static List<Transposition> getMinimalTransposition(int[] data) {
        int length = data.length;
        List<Transposition> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int index = 0;

            for (int j = i; j < length; j++) {
                if (data[j] == i) {
                    index = j;
                    break;
                }
            }

            if (index == i) continue;// right position skip transposition

            Transposition e = new Transposition(i, index);
            list.add(e);
            e.swap(data);
        }
        return list.reversed();
    }

    // all indexes
    public record Transposition(int first, int second) {
        public void swap(int[] data) {
            int temp = data[second];
            data[second] = data[first];
            data[first] = temp;
        }

        public <T> void operate(T[] array, BiConsumer<T, T> operation) {
            operation.accept(array[first], array[second]);
        }
    }
}
