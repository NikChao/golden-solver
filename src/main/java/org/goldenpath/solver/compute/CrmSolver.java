package org.goldenpath.solver.compute;

import org.goldenpath.solver.compute.model.CrmInput;

/**
 * Counterfactual regret minimization solver
 */
public class CrmSolver {
    private final HandResolver handResolver;

    public CrmSolver(HandResolver handResolver) {
        this.handResolver = handResolver;
    }

    public void solve(CrmInput input) {
        System.out.println("Solving!");
    }
}
