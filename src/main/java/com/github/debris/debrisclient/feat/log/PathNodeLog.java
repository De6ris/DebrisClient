package com.github.debris.debrisclient.feat.log;

import java.util.Map;

public class PathNodeLog extends AbstractGameLog {
    private static final String ONLY_NAMED = "only_named";

    @Override
    protected void registerOptions(Map<String, String> map) {
        map.put(ONLY_NAMED, String.valueOf(false));
    }

    public boolean onlyNamed() {
        return Boolean.parseBoolean(this.getOption(ONLY_NAMED));
    }
}
