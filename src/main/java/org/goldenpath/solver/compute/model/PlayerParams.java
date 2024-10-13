package org.goldenpath.solver.compute.model;

public record PlayerParams(
        String oopRange,
        double[] oopFlopBetSizes,
        double[] oopFlopRaiseSizes,
        double[] oopTurnBetSizes,
        double[] oopTurnRaiseSizes,
        double[] oopRiverBetSizes,
        double[] oopRiverRaiseSizes) {
}
