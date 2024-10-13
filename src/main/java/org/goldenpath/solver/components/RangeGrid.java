package org.goldenpath.solver.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.goldenpath.solver.compute.RangeProvider;

import java.util.ArrayList;
import java.util.List;

public class RangeGrid {
    private record HandLabel(int row, int col, String hand, Label label, StackPane stackPane) {
    }

    // Poker hands grid with suited and offsuit representations
    private static final String[][] HANDS = {
            {"", "A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"},
            {"A", "AA", "AKs", "AQs", "AJs", "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s"},
            {"K", "AK", "KK", "KQs", "KJs", "KTs", "K9s", "K8s", "K7s", "K6s", "K5s", "K4s", "K3s", "K2s"},
            {"Q", "AQ", "KQ", "QQ", "QJs", "QTs", "Q9s", "Q8s", "Q7s", "Q6s", "Q5s", "Q4s", "Q3s", "Q2s"},
            {"J", "AJ", "KJ", "QJ", "JJ", "JTs", "J9s", "J8s", "J7s", "J6s", "J5s", "J4s", "J3s", "J2s"},
            {"T", "AT", "KT", "QT", "JT", "TT", "T9s", "T8s", "T7s", "T6s", "T5s", "T4s", "T3s", "T2s"},
            {"9", "A9", "K9", "Q9", "J9", "T9", "99", "98s", "97s", "96s", "95s", "94s", "93s", "92s"},
            {"8", "A8", "K8", "Q8", "J8", "T8", "98", "88", "87s", "86s", "85s", "84s", "83s", "82s"},
            {"7", "A7", "K7", "Q7", "J7", "T7", "97", "87", "77", "76s", "75s", "74s", "73s", "72s"},
            {"6", "A6", "K6", "Q6", "J6", "T6", "96", "86", "76", "66", "65s", "64s", "63s", "62s"},
            {"5", "A5", "K5", "Q5", "J5", "T5", "95", "85", "75", "65", "55", "54s", "53s", "52s"},
            {"4", "A4", "K4", "Q4", "J4", "T4", "94", "84", "74", "64", "54", "44", "43s", "42s"},
            {"3", "A3", "K3", "Q3", "J3", "T3", "93", "83", "73", "63", "53", "43", "33", "32s"},
            {"2", "A2", "K2", "Q2", "J2", "T2", "92", "82", "72", "62", "52", "42", "32", "22"}
    };

    private RangeProvider ranges;
    private List<HandLabel> handLabels = new ArrayList<HandLabel>();
    Label rangesLabel;
    private Label title;
    private double initialPercentage;

    public RangeGrid(String title, RangeProvider ranges, double initialPercentage) {
        this.ranges = ranges;
        this.rangesLabel = new Label("Range: " + ranges.range);
        this.title = new Label(title);
        this.initialPercentage = initialPercentage;
    }

    public Pane getRangeGrid() {
        // Create a GridPane to hold the poker hands chart
        var gridPane = new GridPane();

        var rangeRow = new HBox(10);
        rangeRow.setPadding(new Insets(10, 10, 10, 10));
        var spacer = new Region();
        // Set the spacer to grow horizontally (this will push the button to the right)
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var copyRangeButton = new Button("Copy");
        var tooltip = new Tooltip("Copied!");
        Tooltip.install(copyRangeButton, tooltip);
        copyRangeButton.setMinWidth(40);
        copyRangeButton.alignmentProperty().set(Pos.CENTER_RIGHT);
        copyRangeButton.setOnMouseClicked((MouseEvent event) -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(ranges.range);
            clipboard.setContent(content);

            // Show the tooltip
            tooltip.show(copyRangeButton,
                    copyRangeButton.getLayoutX() + 165,
                    copyRangeButton.getLayoutY() + 125); // Position it below the button

            // Hide the tooltip after 2 seconds
            var pauseTransition = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
            pauseTransition.setOnFinished((e) -> tooltip.hide());
            pauseTransition.play();
        });
        rangesLabel.setMaxWidth(450);
        rangesLabel.setWrapText(false);
        rangesLabel.setTextOverrun(OverrunStyle.ELLIPSIS);

        rangeRow.getChildren().addAll(rangesLabel, spacer, copyRangeButton);


        // Create a slider with a range from 1 to 100
        var slider = new Slider(1, 100, initialPercentage);
        slider.setMinWidth(500);
        slider.setMaxWidth(500);
        slider.setShowTickLabels(true); // Show tick labels on the slider
        slider.setShowTickMarks(true); // Show tick marks on the slider

        // Create a label to display the current value
        var valueLabel = new Label(new StringBuilder().append(initialPercentage).append('%').toString());

        // Add a listener to update the label when the slider value changes
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            valueLabel.setText(newValue.intValue() + "%");
            ranges.updatePokerRange(newValue.intValue());
            rangesLabel.setText("Range: " + ranges.range);
            updateAllStyles();
        });

        // Create a VBox to hold the slider and the label
        var sliderBox = new HBox(10);
        sliderBox.setMaxWidth(450);
        sliderBox.setMinWidth(450);
        sliderBox.setAlignment(Pos.TOP_LEFT);
        sliderBox.getChildren().addAll(slider, valueLabel);

        // Populate the grid with poker hands
        for (int row = 0; row < HANDS.length; row++) {
            for (int col = 0; col < HANDS[row].length; col++) {
                var hand = HANDS[row][col];
                var stackPane = new StackPane();
                var clip = new Rectangle(40, 20);
                stackPane.setClip(clip);
                stackPane.setStyle("-fx-border-color: darkgrey;");
                stackPane.setOnMouseClicked((MouseEvent event) -> {
                    ranges.toggleHand(hand);
                    updateAllStyles();
                    rangesLabel.setText("Ranges: " + ranges.range);
                });

                var handLabel = new Label(hand);
                handLabel.setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");

                handLabel.setMinSize(40, 20); // Set minimum size for labels

                handLabels.add(new HandLabel(row, col, hand, handLabel, stackPane));

                stackPane.getChildren().add(handLabel);
                // Add the hand label to the grid
                gridPane.add(stackPane, col, row);
            }
        }

        updateAllStyles();

        var layout = new VBox();
        layout.getChildren().addAll(title, sliderBox, rangeRow, gridPane);
        return layout;
    }

    public void updateAllStyles() {
        for (HandLabel handLabel : handLabels) {
            if (handLabel.row != 0 && handLabel.col != 0) {
                var handIsSelected = ranges.findMatchingHandInRange(handLabel.hand).isPresent();
                var frequency = handIsSelected ? findHandFrequency(handLabel.hand) : 0;

                // Background rectangle (unfilled part)
                var background = new Rectangle(40, 20);
                background.setFill(Color.LIGHTGRAY);

                // Filled rectangle (to represent frequency, vertically)
                var fill = new Rectangle(40, 20 * frequency); // Adjust height based on frequency
                fill.setFill(Color.GREEN);
                fill.setTranslateY(20 * (1 - frequency)); // Adjust the position to start from the bottom

                handLabel.stackPane.getChildren().setAll(background, fill, handLabel.label);
            } else {
                handLabel.label.setStyle("-fx-background-color: grey; -fx-alignment: CENTER;");
            }

        }
    }

    private double findHandFrequency(String hand) {
        // Split the handRange string by commas to get individual hand entries
        var hands = ranges.range.replaceAll(" ", "").split(",");

        // Iterate through each hand in the range
        for (var entry : hands) {
            // Check if the hand has a frequency (contains ':')
            if (entry.contains(":")) {
                // Split by ':' to separate hand and frequency
                var parts = entry.split(":");
                var handName = parts[0];
                double frequency = Double.parseDouble(parts[1]);

                // If the hand matches the one we're searching for, return its frequency
                if (handName.equals(hand)) {
                    return frequency;
                }
            } else {
                // No frequency specified, assume it's 1.0
                if (entry.equals(hand)) {
                    return 1.0;
                }
            }
        }

        // If the hand is not found in the range, return -1 to indicate that
        return -1.0;
    }
}
