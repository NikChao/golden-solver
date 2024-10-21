package org.goldenpath.solver.compute.model;

public record CrmInput(
        PlayerParams[] players,
        int raiseLimit,
        int pot,
        int effectiveStack,
        double allinThreshold,
        String[] boardCards
) {
}
