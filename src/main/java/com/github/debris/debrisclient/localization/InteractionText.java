package com.github.debris.debrisclient.localization;

public enum InteractionText implements Translatable {
    NO_MATCHING_BLOCKS,
    FOUND_BLOCKS,
    NO_MATCHING_ENTITIES,
    FOUND_ENTITIES,
    STOP_BLOCKS,
    STOP_ENTITIES,
    ;

    @Override
    public String getTranslationKey() {
        return "debris_client.interaction." + this.name().toLowerCase();
    }
}
