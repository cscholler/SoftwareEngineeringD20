package edu.wpi.cs3733.d20.teamL.util;

public class MathUtils {
    public static double lerp(double min, double max, double progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("Progress for lerp must be between 0 and 1 (inclusive), found " + progress);
        if (min > max)
            throw new IllegalArgumentException("max must be greater than min for lerp, got min: " + min + " max: " + max);

        return min + ((max - min) * progress);
    }

    public static double invLerp(double min, double max, double value) {
        if (value < min || value > max)
            throw new IllegalArgumentException("Progress for invLerp must be between min (" + min + ") and max (" + max + ")(inclusive), found " + value);
        if (min > max)
            throw new IllegalArgumentException("max must be greater than min for invLerp, got min: " + min + " max: " + max);

        return (value - min) / (max - min);
    }

    public static double bound(double min, double max, double value) {
        return Math.min(Math.max(value, min), max);
    }
}
