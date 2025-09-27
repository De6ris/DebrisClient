package com.github.debris.debrisclient.localization;

public enum AutoProcessText implements Translatable {
    CONTAINER_TAKER_MESSAGE("container_taker.message"),
    ITEM_FINDER_FOUND("item_finder.found"),
    ITEM_FINDER_NOT_FOUND("item_finder.not_found"),
    TEMPLATE_FILLER_SIZE_UNMATCH("template_filler.size_unmatch"),
    TEMPLATE_FILLER_SUCCESS("template_filler.success"),
    TEMPLATE_RECORDER_MESSAGE("template_recorder.message"),
    ;

    private final String key;

    AutoProcessText(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve("auto_processor").resolve(this.key).build();
    }
}
