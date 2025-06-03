package com.github.debris.debrisclient.config.early;

import com.github.debris.debrisclient.DebrisClient;

public class DCEarlyConfig extends ConfigBase {

    @ConfigEntry(key = "carpetFix", booleanDefault = true)
    public boolean CarpetFix = true;

    public DCEarlyConfig() {
        super(DebrisClient.CONFIG_DIR.resolve("config_early.prop").toString());
    }

    private static DCEarlyConfig Instance;

    public static DCEarlyConfig getInstance() {
        return Instance == null ? (Instance = createInstance()) : Instance;
    }

    private static DCEarlyConfig createInstance() {
        DCEarlyConfig DCEarlyConfig = new DCEarlyConfig();
        DCEarlyConfig.loadConfig();
        return DCEarlyConfig;
    }

    private boolean dirty = false;

    public void markDirty() {
        this.dirty = true;
    }

    public void refresh() {
        if (this.dirty) this.saveConfig();
        this.dirty = false;
    }
}
