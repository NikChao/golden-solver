package org.goldenpath.solver.compute.model;

import org.goldenpath.solver.components.OtherParams;
import org.goldenpath.solver.components.PositionParams;

public record CrmInput(
        PlayerParams[] players,

        int raiseLimit,
        int pot,
        int effectiveStack,
        double allinThreshold,

        String boardCards
) {
}
