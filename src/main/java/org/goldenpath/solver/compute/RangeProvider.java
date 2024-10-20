package org.goldenpath.solver.compute;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.ArrayList;
import java.util.Optional;

public class RangeProvider {
    // List of all possible hands in poker in order of pre-flop equity.
    // http://iholdemindicator.com/rank.html
    private static final String[] ALL_HANDS = {
            "AA", "KK", "QQ", "AKs", "JJ", "AQs", "KQs", "AJs", "KJs", "TT", "AK", "ATs", "QJs", "KTs", "QTs", "JTs",
            "99", "AQ", "A9s", "KQ", "88", "K9s", "T9s", "A8s", "Q9s", "J9s", "AJ", "A5s", "77", "A7s", "KJ", "A4s",
            "A3s", "A6s", "QJ", "66", "K8s", "T8s", "A2s", "98s", "J8s", "AT", "Q8s", "K7s", "KT", "55", "JT", "87s",
            "QT", "44", "22", "33", "K6s", "97s", "K5s", "76s", "T7s", "K4s", "K2s", "K3s", "Q7s", "86s", "65s", "J7s",
            "54s", "Q6s", "75s", "96s", "Q5s", "64s", "Q4s", "Q3s", "T9", "T6s", "Q2s", "A9", "53s", "85s", "J6s", "J9",
            "K9", "J5s", "Q9", "43s", "74s", "J4s", "J3s", "95s", "J2s", "63s", "A8", "52s", "T5s", "84s", "T4s", "T3s",
            "42s", "T2s", "98", "T8", "A5", "A7", "73s", "A4", "32s", "94s", "93s", "J8", "A3", "62s", "92s", "K8", "A6",
            "87", "Q8", "83s", "A2", "82s", "97", "72s", "76", "K7", "65", "T7", "K6", "86", "54", "K5", "J7", "75", "Q7",
            "K4", "K3", "96", "K2", "64", "Q6", "53", "85", "T6", "Q5", "43", "Q4", "Q3", "74", "Q2", "J6", "63", "J5",
            "95", "52", "J4", "J3", "42", "J2", "84", "T5", "T4", "32", "T3", "73", "T2", "62", "94", "93", "92", "83",
            "82", "72"
    };

    public String range = "AA";

    public RangeProvider() {
    }

    public RangeProvider(double percentage) {
        updatePokerRange(percentage);
    }

    public void updatePokerRange(double percentage) {
        // Validate the percentage
        var selectedHands = getSelectedHands(percentage);

        // Build the resulting range string
        var rangeBuilder = new StringBuilder();
        for (var hand : selectedHands) {
            rangeBuilder.append(hand).append(",");
        }

        // Remove the last comma and space, if there are any hands selected
        if (rangeBuilder.length() > 0) {
            rangeBuilder.setLength(rangeBuilder.length() - 1); // Remove the trailing ", "
        }

        range = rangeBuilder.toString();
    }

    public void toggleHand(String hand) {
        Optional<String> matchingHandInRange = findMatchingHandInRange(hand);
        if (!matchingHandInRange.isPresent()) {
            var inputDialog = new TextInputDialog("0.5");
            inputDialog.setTitle("Frequency");
            inputDialog.setHeaderText("Enter frequency");
            inputDialog.setContentText("Frequency:");
            Button okButton = (Button) inputDialog.getDialogPane().lookupButton(ButtonType.OK);

            inputDialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                okButton.setDisable(!isValidFrequency(newValue));
            });
            var result = inputDialog.showAndWait();

            if (result.isPresent()) {
                var handWithFreq = hand + ":" + result.get();
                if (range.length() > 0) {
                    range += ",";
                }
                range += handWithFreq;
            }
        } else {
            String handPattern = matchingHandInRange.get() + "(?::\\d+(\\.\\d+)?)?";

            // Replace the hand and any trailing commas (for cleanliness)
            range = range.replaceAll("\\b" + handPattern + "\\b,?", "")
                    .replaceAll(",{2,}", ",")  // Remove double commas
                    .replaceAll("^,|,$", "");  // Remove leading or trailing commas

        }
    }

    public Optional<String> findMatchingHandInRange(String hand) {
        return findMatchingHandInRange(hand, range);
    }

    protected Optional<String> findMatchingHandInRange(String hand, String range) {
        for (var includedHand : range.split(",")) {
            if (includedHand.equals(hand) || (includedHand.contains(":") && includedHand.split(":")[0].equals(hand))) {
                return Optional.of(includedHand);
            }
        }

        return Optional.empty();
    }

    private static ArrayList<String> getSelectedHands(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100.");
        }

        // Calculate the number of hands to include based on the percentage
        int totalHands = ALL_HANDS.length;
        int handsToInclude = (int) Math.round(totalHands * (percentage / 100));

        // Create a list to hold the selected hands
        var selectedHands = new ArrayList<String>();

        // Add the top hands to the selected hands list
        for (int i = 0; i < handsToInclude; i++) {
            selectedHands.add(ALL_HANDS[i]);
        }
        return selectedHands;
    }

    private static boolean isValidFrequency(String input) {
        try {
            var value = Double.parseDouble(input);
            return value > 0 && value <= 1;
        } catch (Exception e) {
            return false;
        }
    }
}
