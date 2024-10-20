package org.goldenpath.solver.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class Title {
    public Pane title;

    public Title() {
        var titleLeft = new Label("Golden");
        titleLeft.setStyle("-fx-text-fill: gold; -fx-font-size: 24px; -fx-font-weight: semibold;");
        var titleRight = new Label("Solver");
        titleRight.setStyle("-fx-font-size: 24px; -fx-font-weight: semibold;");
        var title = new HBox(titleLeft, titleRight);
        title.setStyle("-fx-padding-top: 24px; -fx-padding-bottom: 24px;");
        this.title = title;

    }

    public Pane render() {
        return title;
    }
}
