package com.github.debris.debrisclient.feat.log;

import com.github.debris.debrisclient.feat.FutureTaskQueue;
import com.github.debris.debrisclient.localization.GameLogText;
import com.github.debris.debrisclient.util.ChatUtil;
import com.github.debris.debrisclient.util.TextFactory;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortalLog extends AbstractGameLog {
    public void onPortalCreated(World world, BlockPos lowerLeft) {
        FutureTaskQueue.addNextTick(() -> ChatUtil.addLocalMessage(
                        Text.empty()
                                .append(TextFactory.DEBUG)
                                .append(ScreenTexts.SPACE)
                                .append(GameLogText.PORTAL_CREATED.text(lowerLeft.toShortString(), world.getRegistryKey().getValue()))
                )
        );
    }
}
