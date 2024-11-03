package org.goldenpath.solver.components;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Practice {
    private static final String[][][] SCENARIOS = {
            new String[][]{
                    new String[]{"SB", "Ah", "Kc", "50"},
                    new String[]{"BB", "2c", "3d", "100"},
                    new String[]{"UTG", "4h", "5s", null},
                    new String[]{"UTG1", "6d", "7c", null},
                    new String[]{"UTG2", "8h", "9s", null},
                    new String[]{"LJ", "7d", "2c", null},
                    new String[]{"HJ", "Jd", "Qc", "200"},
                    new String[]{"CO", "Td", "3c", null},
                    new String[]{"BTN", "Ac", "8c", "ACT", "CALL:0.03,FOLD:0,ALLIN:-0.02"},
            },
            new String[][]{
                    new String[]{"SB", "7h", "2h", "50"},
                    new String[]{"BB", "2c", "3d", "100"},
                    new String[]{"UTG", "Ah", "Qh", "230"},
                    new String[]{"UTG1", "8c", "8d", "230"},
                    new String[]{"LJ", "Jd", "Qc", null},
                    new String[]{"HJ", "Jd", "Qc", null},
                    new String[]{"CO", "Td", "9d", "ACT", "RAISE:26.1,FOLD:0,ALLIN:25.0"},
                    new String[]{"BTN", "Ac", "8c", null}
            }
    };

    public Tab tab() {
        var container = new VBox(10);
        container.setPadding(new Insets(16, 16, 16, 16));

        AtomicInteger scenarioIndex = new AtomicInteger();
        AtomicReference<String[][]> scenario = new AtomicReference<>(SCENARIOS[scenarioIndex.get()]);

        var actions = new HBox(10);
        var fold = new Button("Fold");
        var call = new Button("Call");
        var allIn = new Button("All in");
        actions.getChildren().addAll(fold, call, allIn);

        var bestStrategy = "";
        var bestEv = Double.MIN_VALUE;
        var strategyMap = new HashMap<String, Double>();
        for (String[] player : scenario.get()) {
            if (!"ACT".equals(player[3])) continue;

            var strategies = player[4].split(",");
            for (var strategyPair : strategies) {
                var splitStrategyPair = strategyPair.split(":");
                var strategy = splitStrategyPair[0];
                var ev = Double.parseDouble(splitStrategyPair[1]);
                strategyMap.put(strategy, ev);
                if (ev > bestEv) {
                    bestEv = ev;
                    bestStrategy = strategy;
                }
            }
        }

        final var finalBestStrategy = bestStrategy;
        final var finalBestEv = bestEv;

        allIn.setOnMouseClicked(click -> {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            if (!"ALLIN".equals(finalBestStrategy)) {
                alert.setTitle("Wrong!");
                alert.setContentText("Incorrect decision: -" + strategyMap.get("FOLD"));
            } else {
                alert.setTitle("Correct!");
                alert.setContentText("Correct decision: +" + finalBestEv);
            }
            alert.show();
        });


        fold.setOnMouseClicked(click -> {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            if (!"FOLD".equals(finalBestStrategy)) {
                alert.setTitle("Wrong!");
                alert.setContentText("Incorrect decision: -" + strategyMap.get("FOLD"));
            } else {
                alert.setTitle("Correct!");
                alert.setContentText("Correct decision: +" + finalBestEv);
            }
            alert.show();
        });

        call.setOnMouseClicked(click -> {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            if (!"CALL".equals(finalBestStrategy)) {
                alert.setTitle("Wrong!");
                alert.setContentText("Incorrect decision: -" + strategyMap.get("CALL"));
            } else {
                alert.setTitle("Correct!");
                alert.setContentText("Correct decision: +" + finalBestEv);
            }
            alert.show();

            if (SCENARIOS.length > (scenarioIndex.get() + 1)) {
                scenarioIndex.getAndIncrement();
                scenario.set(SCENARIOS[scenarioIndex.get()]);

                container.getChildren().setAll(title(), table(scenario.get()), actions);
            }
        });

        container.getChildren().setAll(title(), table(scenario.get()), actions);

        return new Tab("Practice", container);
    }

    private static Pane title() {
        var titleLeft = new javafx.scene.control.Label("Golden");
        titleLeft.setStyle("-fx-text-fill: gold; -fx-font-size: 24px; -fx-font-weight: semibold;");
        var titleRight = new javafx.scene.control.Label("Trainer");
        titleRight.setStyle("-fx-font-size: 24px; -fx-font-weight: semibold;");
        var title = new HBox(titleLeft, titleRight);
        title.setStyle("-fx-padding-top: 24px; -fx-padding-bottom: 24px;");
        return title;
    }

    private static Pane table(String[][] scenario) {
        // Create a Canvas for drawing
        Canvas tableCanvas = new Canvas(800, 600);
        GraphicsContext gc = tableCanvas.getGraphicsContext2D();

        // Draw the poker table with players
        drawPokerTable(gc, scenario, 8);

        var table = new BorderPane();
        table.setTop(tableCanvas);

        return table;
    }

    /**
     * Draws a 6-handed poker table with players and their hole cards.
     *
     * @param gc       The GraphicsContext for drawing
     * @param scenario A list of players with hole cards and actions / the acting player should have a list of options with corresponding EVs
     */
    private static void drawPokerTable(GraphicsContext gc, String[][] scenario, int dealerIndex) {
        // Clear the canvas
        gc.clearRect(0, 0, 800, 600);

        // Draw wooden background
        gc.setFill(Color.SADDLEBROWN);
        gc.fillOval(80, 80, 540, 440); // Larger oval for wooden border

        // Create a gradient for the felt table center
        RadialGradient feltGradient = new RadialGradient(
                0, 0.5, 400, 400, 250, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.DARKGREEN),
                new Stop(1, Color.DARKOLIVEGREEN)
        );
        gc.setFill(feltGradient);
        gc.fillOval(90, 90, 520, 420); // Inner oval for felt

        // Table radii and player positioning radii for oval shape
        double playerRadiusX = 300; // Slightly outside the table horizontally
        double playerRadiusY = 240; // Slightly outside the table vertically

        // Center of the table
        double centerX = 330;
        double centerY = 275;

        // Loop through players and position them around the oval
        for (int i = 0; i < scenario.length; i++) {
            var player = scenario[i];

            // Calculate player's position using an oval (elliptical) layout
            double angle = 2 * Math.PI / scenario.length * i;
            double playerX = centerX + playerRadiusX * Math.cos(angle);
            double playerY = centerY + playerRadiusY * Math.sin(angle);

            // Draw player name
            // Set text color and font
            gc.setFill(i == dealerIndex ? Color.GOLD : Color.WHITE);
            gc.setFont(new Font("Arial", 18));
            gc.fillText(player[0], playerX - 30, playerY - 20);

            var decrement = (player[3] != null && !player[3].equals("ACT")) ? Integer.parseInt(player[3]) : 0;
            gc.fillText("Stack: " + (100 - decrement), playerX - 30, playerY);

            // Draw hole cards
            drawCard(gc, player[1], playerX - 30, playerY + 10, i == 0);
            drawCard(gc, player[2], playerX + 20, playerY + 10, i == 0);

            // Draw chip stack
            double betX = centerX + (playerX - centerX) * 0.6; // Halfway between player and center
            double betY = centerY + (playerY - centerY) * 0.6;
            var playerAction = player[3];

            if (playerAction == null) {
                gc.setFill(Color.WHITE);
                gc.fillText("Fold", betX - 20, betY + 20);
            } else if (playerAction.equals("ACT")) {
                gc.setFill(Color.GOLD);
                gc.fillText("Your move", betX - 20, betY + 20);
            } else {
                var bet = Integer.valueOf(player[3]);
                drawChips(gc, bet, betX, betY);
                gc.setFill(Color.WHITE);
                gc.setFont(new Font("Arial", 14));
                if (bet > 0) {
                    gc.fillText("Bet: " + bet, betX - 20, betY + 20);
                } else {
                    gc.fillText("Check", betX - 20, betY + 20);
                }

            }

            if (i == dealerIndex) {
                drawDealerChip(gc, playerX - 60, playerY - 60);
            }
        }
    }

    /**
     * Draws a stack of chips at the specified position, representing the player's chip count.
     *
     * @param gc    The GraphicsContext for drawing
     * @param stack The number of chips in the stack
     * @param x     The x-coordinate of the stack's base
     * @param y     The y-coordinate of the stack's base
     */
    private static void drawChips(GraphicsContext gc, int stack, double x, double y) {
        double chipHeight = 5; // Height of each chip
        Color chipColor = Color.PURPLE; // Color of the chips

        // Draw chips as stacked circles
        for (int i = 0; i < stack / 20; i++) {
            gc.setFill(chipColor);
            gc.fillOval(x, y - i * chipHeight, 20, 5); // Draw each chip slightly above the previous one
            gc.setStroke(Color.BLACK);
            gc.strokeOval(x, y - i * chipHeight, 20, 5); // Outline for each chip
        }
    }

    /**
     * Draws a single card at the specified position.
     *
     * @param gc   The GraphicsContext for drawing
     * @param card The card to draw (e.g., "AS" for Ace of Spades)
     * @param x    The x-coordinate
     * @param y    The y-coordinate
     */
    private static void drawCard(GraphicsContext gc, String card, double x, double y, boolean faceUp) {
        // Draw card background
        gc.setFill(Color.WHITE);
        gc.fillRoundRect(x, y, 40, 60, 10, 10);

        // Draw card border
        gc.setStroke(Color.BLACK);
        gc.strokeRoundRect(x, y, 40, 60, 10, 10);

        // Draw card text
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 14));

        if (faceUp) {
            gc.fillText(toCardLabel(card), x + 8, y + 30);
        } else {
            gc.setFill(Color.GOLD);
            gc.fillText("G", x + 10, y + 30);
            gc.setFill(Color.BLACK);
            gc.fillText("S", x + 21, y + 30);
        }
    }

    private static String toCardLabel(String card) {
        return card
                .replace("h", "❤️")
                .replace("d", "♦️")
                .replace("c", "♣️")
                .replace("s", "♠️");
    }

    /**
     * Draws a circular dealer chip with "Dealer" text at the specified position.
     *
     * @param gc The GraphicsContext for drawing
     * @param x  The x-coordinate for the dealer chip
     * @param y  The y-coordinate for the dealer chip
     */
    private static void drawDealerChip(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.YELLOW);
        gc.fillOval(x, y, 30, 30); // Draw the chip as a small circle

        // Draw text "D" in the center of the dealer chip
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 16));
        gc.fillText("D", x + 9, y + 20);
    }

}
