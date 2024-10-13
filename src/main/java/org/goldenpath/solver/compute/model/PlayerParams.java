package org.goldenpath.solver.compute.model;

public record PlayerParams(
        String range,
        double[] flopBetSizes,
        double[] flopRaiseSizes,
        double[] turnBetSizes,
        double[] turnRaiseSizes,
        double[] riverBetSizes,
        double[] riverRaiseSizes) {
}
