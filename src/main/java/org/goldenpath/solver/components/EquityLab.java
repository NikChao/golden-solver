package org.goldenpath.solver.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.goldenpath.solver.compute.EquityCalculator;
import org.goldenpath.solver.compute.RangeProvider;
import org.goldenpath.solver.data.RangeConverter;

import java.util.Arrays;

public class EquityLab {
    private final EquityCalculator equityCalculator;
    private final RangeGrid villainRangeGrid;
    private final RangeProvider villainRange;
    private final RangeConverter rangeConverter;

    public EquityLab(EquityCalculator equityCalculator, RangeGrid villainRangeGrid, RangeProvider villainRange, RangeConverter rangeConverter) {
        this.equityCalculator = equityCalculator;
        this.villainRangeGrid = villainRangeGrid;
        this.villainRange = villainRange;
        this.rangeConverter = rangeConverter;

    }

    public Pane render() {
        var container = new VBox(24);
        container.setStyle("-fx-padding: 16;");

        var heroHand = new TextField("Qh,5d");
        var heroHandContainer = new HBox(10);
        var heroHandLabel = new Label("Hero's hand: ");
        heroHandContainer.getChildren().addAll(heroHandLabel, heroHand);

        var board = new TextField("Ts,9d,As");
        var boardContainer = new HBox(10);
        var boardLabel = new Label("Board: ");
        boardContainer.getChildren().addAll(boardLabel, board);

        var findEquityButton = new Button("Find equity");
        var heroEquityLabel = new Label("Hero: ???");
        var villainEquityLabel = new Label("Villain: ???");
        var equityContainer = new VBox(10);
        var equityInnerContainer = new HBox(10);
        var equityLabel = new Label("Equity");
        equityInnerContainer.getChildren().addAll(heroEquityLabel, villainEquityLabel);
        equityContainer.getChildren().addAll(equityLabel, equityInnerContainer);

        var heroOddsLabel = new Label("Hero: ???");
        var villainOddsLabel = new Label("Hero: ???");
        var chopOddsLabel = new Label("Chop: ???");
        var oddsContainer = new VBox(10);
        var oddsInnerContainer = new HBox(10);
        oddsInnerContainer.getChildren().addAll(heroOddsLabel, villainOddsLabel, chopOddsLabel);
        var oddsLabel = new Label("Odds");
        oddsContainer.getChildren().addAll(oddsLabel, oddsInnerContainer);

        var equityLabTitle = title();

        container.getChildren().addAll(
                equityLabTitle,
                villainRangeGrid.getRangeGrid(false),
                heroHandContainer,
                boardContainer,
                findEquityButton,
                equityContainer,
                oddsContainer);

        findEquityButton.setOnMouseClicked(e -> {
            var villainSuitedRange = Arrays.stream(rangeConverter.toSuitedRange(villainRange.range)).map(handText -> new String[]{
                    new String(new char[]{handText.charAt(0), handText.charAt(1)}),
                    new String(new char[]{handText.charAt(2), handText.charAt(3)})
            }).toArray(String[][]::new);

            var equity = equityCalculator.handVsRange(
                    heroHand.getText().split(","),
                    villainSuitedRange,
                    board.getText().split(",")
            );

            heroOddsLabel.setText("Hero: " + round(equity[0]) + '%');
            heroEquityLabel.setText("Hero: " + round(equity[0] + (equity[2] / 2)) + '%');
            villainOddsLabel.setText("Villain: " + round(equity[1]) + '%');
            villainEquityLabel.setText("Villain: " + round(equity[1] + (equity[2] / 2)) + '%');
            chopOddsLabel.setText("Chop: " + round(equity[2]) + '%');
        });

        return container;
    }

    private static Pane title() {
        var titleLeft = new Label("Equity");
        titleLeft.setStyle("-fx-text-fill: gold; -fx-font-size: 24px; -fx-font-weight: semibold;");
        var titleRight = new Label("Lab 🧪");
        titleRight.setStyle("-fx-font-size: 24px; -fx-font-weight: semibold;");
        var title = new HBox(titleLeft, titleRight);
        title.setStyle("-fx-padding-top: 24px; -fx-padding-bottom: 24px;");
        return title;
    }

    private static double round(double value) {
        return Math.round(value * 100);
    }
}
