package com.github.debris.debrisclient.inventory.autoprocess;

public enum ProcessResult {
    SKIP(false, false),
    CLOSE_TERMINATE(true, true),
    OPEN_TERMINATE(false, true),
    ;

    final boolean closeGui;
    final boolean terminate;

    ProcessResult(boolean closeGui, boolean terminate) {
        this.closeGui = closeGui;
        this.terminate = terminate;
    }

    public boolean closeGui() {
        return this.closeGui;
    }

    public boolean terminate() {
        return this.terminate;
    }
}
