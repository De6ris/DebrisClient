package com.github.debris.debrisclient.localization;

public enum SyncContainerText implements Translatable {
    WAIT_CONTAINER_OPEN,
    NO_CONTAINER_PRESENT,
    ;


    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve("sync_container").resolve(this).build();
    }
}
