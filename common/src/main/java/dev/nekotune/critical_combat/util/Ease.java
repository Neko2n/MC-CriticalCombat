package dev.nekotune.critical_combat.util;

import net.minecraft.util.Mth;

public class Ease {
    public static double inQuad(final double x) {
        return x * x;
    }

    public static double inOutSine(final float x) {
        return -(Mth.cos((float) Math.PI * x) - 1) / 2;
    }

    public static double outBack(final double x) {
        return 1D + 2.70158f * Math.pow(x - 1D, 3D) + 1.70158f * Mth.square(x - 1D);
    }

    public static double inOutExpo(final double x) {
        return x < 0.5D
                ? (x == 0D ? 0D : Math.pow(2D, 20D * x - 10D) / 2D)
                : (x == 1D ? 1D : (2D - Math.pow(2D, -20D * x + 10D)) / 2D);
    }
}
