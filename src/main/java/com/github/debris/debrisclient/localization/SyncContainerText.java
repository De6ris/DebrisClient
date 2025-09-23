package com.github.debris.debrisclient.localization;

public enum SyncContainerText implements Translatable {
    WAIT_CONTAINER_OPEN,
    NO_CONTAINER_PRESENT,
    ;


    @Override
    public String getTranslationKey() {
        return "debris_client.sync_container." + this.name().toLowerCase();
    }
}
