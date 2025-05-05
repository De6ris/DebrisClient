package com.github.debris.debrisclient.unsafe.rei;

import it.unimi.dsi.fastutil.ints.IntList;
import me.shedaniel.rei.api.client.search.method.CharacterUnpackingInputMethod;
import me.shedaniel.rei.api.client.search.method.InputMethod;
import me.shedaniel.rei.impl.client.search.argument.InputMethodMatcher;

import java.util.Optional;

public class ReiPinYinMatcher {
    @SuppressWarnings("all")
    public static Optional<Boolean> matchesFilter(String entryString, String filterText) {
        InputMethod<?> active = InputMethod.active();
        if (active instanceof CharacterUnpackingInputMethod method) {
            boolean contains = InputMethodMatcher.contains(method, IntList.of(entryString.codePoints().toArray()), IntList.of(filterText.codePoints().toArray()));
            return Optional.of(contains);
        }
        return Optional.empty();// pinYin-like input method not set yet
    }
}
