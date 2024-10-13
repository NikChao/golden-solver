package org.goldenpath.solver.components;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class OtherParams {
    public record Values(String raiseLimit, String pot, String effectiveStack, String allinThreshold) {
    }

    private TextField raiseLimitTextField;
    private TextField potTextField;
    private TextField effectiveStackTextField;
    private TextField allinThresholdTextField;

    private Pane otherParamsPane;

    public OtherParams() {
        var container = new VBox(10);
        var firstRowContainer = new HBox(10);
        var secondRowContainer = new HBox(10);

        var raiseLimit = new HBox(10);
        var raiseLimitLabel = new Label("Max raises: ");
        raiseLimitTextField = new TextField("3");
        raiseLimit.getChildren().addAll(raiseLimitLabel, raiseLimitTextField);

        var pot = new HBox(10);
        var potLabel = new Label("Pot: ");
        potTextField = new TextField("50");
        pot.getChildren().addAll(potLabel, potTextField);

        var effectiveStack = new HBox(10);
        var effectiveStackLabel = new Label("Effective stack: ");
        effectiveStackTextField = new TextField("200");
        effectiveStack.getChildren().addAll(effectiveStackLabel, effectiveStackTextField);

        var allinThreshold = new HBox(10);
        var allinThresholdLabel = new Label("All in threshold: ");
        allinThresholdTextField = new TextField("0.67");
        allinThreshold.getChildren().addAll(allinThresholdLabel, allinThresholdTextField);

        firstRowContainer.getChildren().addAll(raiseLimit, pot, effectiveStack);
        secondRowContainer.getChildren().addAll(allinThreshold);
        container.getChildren().addAll(firstRowContainer, secondRowContainer);
        container.setMaxWidth(770);
        container.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 4; -fx-padding: 8;");

        this.otherParamsPane = container;
    }

    public Pane render() {
        return otherParamsPane;
    }

    public Values getValues() {
        return new Values(
                raiseLimitTextField.getText(),
                potTextField.getText(),
                effectiveStackTextField.getText(),
                allinThresholdTextField.getText()
        );
    }
}
