package org.goldenpath.solver.compute;

import org.goldenpath.solver.compute.model.CrmInput;
import org.goldenpath.solver.compute.model.GameTree;

/**
 * Counterfactual regret minimization solver
 */
public class CrmSolver {
    private final HandResolver handResolver;

    public CrmSolver(HandResolver handResolver) {
        this.handResolver = handResolver;
    }

    public GameTree solve(CrmInput input) {
        System.out.println("Solving!");

        return null;
    }
}
