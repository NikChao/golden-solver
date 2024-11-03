package org.goldenpath.solver.components;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.goldenpath.solver.compute.model.GameTree;

public class SolutionView {
    private final GameTree gameTree;
    private final GameTreeView treeView;

    public SolutionView(GameTree gameTree) {
        this.gameTree = gameTree;
        this.treeView = new GameTreeView(gameTree);
    }

    public Pane render() {
        var container = new VBox(10);

        container.getChildren().addAll(treeView.render());

        return container;
    }
}
