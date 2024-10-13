package org.goldenpath.solver.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PositionParams {
    public record Spot(String bet, String raise) {
    }

    public record Values(Spot flop, Spot turn, Spot river) {
    }

    private record SpotPaneFields(Pane pane, TextField betField, TextField raiseField) {
    }

    private final Pane fields;
    private TextField flopBet;
    private TextField flopRaise;
    private TextField turnBet;
    private TextField turnRaise;
    private TextField riverBet;
    private TextField riverRaise;

    public PositionParams(String position) {
        this.fields = getFields(position);
    }

    public Pane render() {
        return fields;
    }

    public Values getValues() {
        return new Values(
                new Spot(flopBet.getText(), flopRaise.getText()),
                new Spot(turnBet.getText(), turnRaise.getText()),
                new Spot(riverBet.getText(), riverRaise.getText()));
    }

    private Pane getFields(String position) {
        var container = new VBox(10);
        var innerContainer = new HBox(10);

        var flop = getSpotFields("Flop");
        this.flopBet = flop.betField;
        this.flopRaise = flop.raiseField;

        var turn = getSpotFields("Turn");
        this.turnBet = turn.betField;
        this.turnRaise = turn.raiseField;

        var river = getSpotFields("River");
        this.riverBet = river.betField;
        this.riverRaise = river.raiseField;

        innerContainer.getChildren().addAll(flop.pane, turn.pane, river.pane);
        var ipParamsLabel = new Label(position);
        container.getChildren().addAll(ipParamsLabel, innerContainer);

        return container;
    }

    private SpotPaneFields getSpotFields(String spot) {
        var container = new VBox(10);
        container.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 4; -fx-padding: 8;");
        var betSize = new TextField("0.25, 0.75, 1.5");
        var betSizeLabel = new Label("Bet size:    ");
        var betSizeContainer = new HBox(10);
        betSizeContainer.setAlignment(Pos.CENTER);
        betSizeContainer.getChildren().addAll(betSizeLabel, betSize);
        var raiseSize = new TextField("0.25, 0.75, 1.5");
        var raiseSizeLabel = new Label("Raise size: ");
        var raiseSizeContainer = new HBox(10);
        raiseSizeContainer.setAlignment(Pos.CENTER_LEFT);
        raiseSizeContainer.getChildren().addAll(raiseSizeLabel, raiseSize);

        var spotLabel = new Label(spot);
        spotLabel.setStyle("-fx-font-weight: semibold; -fx-font-size: 14px;");
        container.getChildren().addAll(spotLabel, betSizeContainer, raiseSizeContainer);

        return new SpotPaneFields(container, betSize, raiseSize);
    }
}
