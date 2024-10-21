package org.goldenpath.solver.components;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.goldenpath.solver.compute.model.GameTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SolutionView {
    private final List<GameTree[]> path;
    private final GameTree gameTree;

    public SolutionView(GameTree gameTree) {
        this.gameTree = gameTree;

        this.path = new ArrayList<>() {
            {
                add(new GameTree[]{gameTree});
            }
        };
    }

    public Pane render() {
        var actionContainer = new HBox(10);
        actionContainer.setPadding(new Insets(16, 16, 16, 16));

        var currentAction = new VBox();
        for (var child : gameTree.children) {
            var labelText = child.action.type().name();
            if (child.action.amount() > 0) {
                labelText += ": " + child.action.amount();
            }
            var label = new Label(labelText);
            currentAction.getChildren().add(label);

            label.setOnMouseClicked(event -> {
                // update path
                // re-render
            });
        }

        actionContainer.getChildren().add(currentAction);
        return actionContainer;
    }

    public void alertResult(Map<String, Map<String, double[]>> result) {
        var resultAlert = new Alert(Alert.AlertType.INFORMATION);
        resultAlert.setTitle("Solution");
        var contents = new StringBuilder();

        var keys = Arrays.copyOfRange(result.get("oop").keySet().toArray(), 0, 30);
        for (var key : keys) {
            var recommendation = result.get("oop").get(key);
            var check = Math.round(recommendation[1] * 100);
            var bet = Math.round(recommendation[2] * 100);

            var line = new StringBuilder()
                    .append(key + " - ")
                    .append("Check: ")
                    .append(check)
                    .append("%, Bet: ")
                    .append(bet)
                    .append("%\n")
                    .toString();
            contents.append(line);
        }

        contents.append("IP should:\n");
        for (var key : keys) {
            var recommendation = result.get("ip").get(key);
            var fold = Math.round(recommendation[0] * 100);
            var call = Math.round(recommendation[1] * 100);
            var raise = Math.round(recommendation[2] * 100);

            var line = new StringBuilder()
                    .append(key + " - ")
                    .append("Fold: ")
                    .append(fold)
                    .append("%, Call: ")
                    .append(call)
                    .append("%, Bet: ")
                    .append(raise)
                    .append("%\n")
                    .toString();
            contents.append(line);
        }

        resultAlert.setHeaderText("Solution");
        resultAlert.setContentText(contents.toString());
        resultAlert.setWidth(800);
        resultAlert.show();
    }
}
