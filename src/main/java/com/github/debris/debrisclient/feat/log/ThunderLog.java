package com.github.debris.debrisclient.feat.log;

import com.github.debris.debrisclient.localization.GameLogText;
import com.github.debris.debrisclient.util.ChatUtil;

public class ThunderLog extends AbstractGameLog {
    private boolean thunder = false;

    public void onThunderSync(boolean thunder) {
        if (this.isInactive()) return;
        if (this.thunder != thunder) {
            ChatUtil.addLocalMessage(thunder ? GameLogText.THUNDER_START.translate() : GameLogText.THUNDER_END.translate());
        }
        this.thunder = thunder;
    }
}
