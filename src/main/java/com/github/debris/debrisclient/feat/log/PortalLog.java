package com.github.debris.debrisclient.feat.log;

import com.github.debris.debrisclient.localization.GameLogText;
import com.github.debris.debrisclient.util.ChatUtil;
import com.github.debris.debrisclient.util.TextFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class PortalLog extends AbstractGameLog {
    public void onPortalCreated(Level world, BlockPos lowerLeft) {
        ChatUtil.addLocalMessageNextTick(
                Component.empty()
                        .append(TextFactory.DEBUG)
                        .append(CommonComponents.SPACE)
                        .append(GameLogText.PORTAL_CREATED.translate(lowerLeft.toShortString(), world.dimension().identifier()))

        );
    }
}
