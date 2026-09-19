package com.github.debris.debrisclient.inventory.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.InventoryUtil;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class HoldInventoryMoving {
    private static Mode MODE = Mode.NONE;
    @Nullable
    private static Slot LAST_MOVED_SLOT = null;

    /**
     * @return If I should cancel left-clicking
     */
    public static boolean start() {
        if (InventoryUtil.getHoveredSlot().isEmpty()) return false;
        if (InventoryUtil.isHoldingItem()) return false;

        MODE = detectMode();

        if (MODE.isNone()) return false;

        run(MODE);
        return true;
    }

    private static Mode detectMode() {
        if (DCCommonConfig.ModifierMoveStack.getKeybind().isKeybindHeld()) {
            return Mode.SINGLE;
        }
        if (DCCommonConfig.ModifierMoveSame.getKeybind().isKeybindHeld()) {
            return Mode.SAME;
        }
        return Mode.NONE;
    }

    public static void stop() {
        if (!MODE.isNone()) {
            LAST_MOVED_SLOT = null;
            MODE = Mode.NONE;
        }
    }

    public static void mouseMove() {
        if (MODE.isNone()) return;
        if (MODE != detectMode()) {
            stop();
            return;
        }
        run(MODE);
    }

    private static void run(Mode mode) {
        Optional<Slot> optional = InventoryUtil.getHoveredSlot();
        if (optional.isEmpty()) return;
        Slot slot = optional.get();
        if (LAST_MOVED_SLOT == slot) return;

        handle(slot, mode);

        LAST_MOVED_SLOT = slot;
    }

    private static void handle(Slot slot, Mode mode) {
        if (mode == Mode.SINGLE) {
            InventoryUtil.maybeQuickMove(slot);
        }
        if (mode == Mode.SAME) {
            InventoryTweaks.tryMoveSame();
        }
    }

    private enum Mode {
        NONE,
        SINGLE,
        SAME,
        ;

        private boolean isNone() {
            return this == NONE;
        }
    }
}
