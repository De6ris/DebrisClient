package com.github.debris.debrisclient.localization;

public class TranslationKeyResolver {
    private final StringBuilder builder;

    public TranslationKeyResolver(String root) {
        this.builder = new StringBuilder(root);
    }

    public TranslationKeyResolver resolve(String s) {
        this.builder.append('.').append(s);
        return this;
    }

    public TranslationKeyResolver resolve(Enum<?> e) {
        return this.resolve(e.name().toLowerCase());
    }

    public String build() {
        return this.builder.toString();
    }
}
