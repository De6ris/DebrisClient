package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.AccessorUtil;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;

public class AutoMoving {
    private static boolean squatFlag = false;

    public static boolean isAutoMoving() {
        return DCCommonConfig.AUTO_WALK.getBooleanValue() ||
                DCCommonConfig.AUTO_LEFT.getBooleanValue() ||
                DCCommonConfig.AUTO_BACK.getBooleanValue() ||
                DCCommonConfig.AUTO_RIGHT.getBooleanValue();
    }

    public static void tickInput(ClientInput input) {
        Input oldInput = input.keyPresses;
        input.keyPresses = new Input(
                oldInput.forward() || DCCommonConfig.AUTO_WALK.getBooleanValue(),
                oldInput.backward() || DCCommonConfig.AUTO_BACK.getBooleanValue(),
                oldInput.left() || DCCommonConfig.AUTO_LEFT.getBooleanValue(),
                oldInput.right() || DCCommonConfig.AUTO_RIGHT.getBooleanValue(),
                oldInput.jump(),
                DCCommonConfig.AUTO_SQUAT.getBooleanValue() ? tickSquat() : oldInput.shift(),
                oldInput.sprint()
        );
        float f = calculateImpulse(input.keyPresses.forward(), input.keyPresses.backward());
        float g = calculateImpulse(input.keyPresses.left(), input.keyPresses.right());
        AccessorUtil.setMoveVector(input, new Vec2(g, f).normalized());
    }

    public static void clearMovement(ClientInput input) {
        input.keyPresses = Input.EMPTY;
        AccessorUtil.setMoveVector(input, Vec2.ZERO);
    }

    private static float calculateImpulse(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }

    private static boolean tickSquat() {
        squatFlag = !squatFlag;
        return squatFlag;
    }
}
