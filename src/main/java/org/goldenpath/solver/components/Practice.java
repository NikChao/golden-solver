package org.goldenpath.solver.components;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Practice {
    public Tab tab() {
        var container = new VBox(10);
        container.setPadding(new Insets(16, 16, 16, 16));
        container.getChildren().addAll(title(), table());

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

    private static Pane table() {
        // Create a Canvas for drawing
        Canvas tableCanvas = new Canvas(800, 800);
        GraphicsContext gc = tableCanvas.getGraphicsContext2D();

        // Example player data
        String[][] players = new String[][]{
                new String[]{"Player 1", "As", "Kh"},
                new String[]{"Player 2", "2c", "3d"},
                new String[]{"Player 3", "4h", "5s"},
                new String[]{"Player 4", "6d", "7c"},
                new String[]{"Player 5", "8h", "9s"},
                new String[]{"Player 6", "Jd", "Qc"}
        };

        // Draw the poker table with players
        drawPokerTable(gc, players);

        var table = new BorderPane();
        table.setCenter(tableCanvas);

        return table;
    }

    /**
     * Draws a 6-handed poker table with players and their hole cards.
     *
     * @param gc      The GraphicsContext for drawing
     * @param players A list of 6 players with names and hole cards
     */
    private static void drawPokerTable(GraphicsContext gc, String[][] players) {
        if (players.length != 6) {
            throw new IllegalArgumentException("This method requires exactly 6 players.");
        }

        // Clear the canvas
        gc.clearRect(0, 0, 800, 800);

        // Draw oval table background
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(150, 200, 500, 400); // Draw an oval table

        // Table radii and player positioning radii for oval shape
        double playerRadiusX = 300; // Slightly outside the table horizontally
        double playerRadiusY = 240; // Slightly outside the table vertically

        // Center of the table
        double centerX = 375;
        double centerY = 350;

        // Loop through players and position them around the oval
        for (int i = 0; i < players.length; i++) {
            var player = players[i];

            // Calculate player's position using an oval (elliptical) layout
            double angle = 2 * Math.PI / players.length * i;
            double playerX = centerX + playerRadiusX * Math.cos(angle);
            double playerY = centerY + playerRadiusY * Math.sin(angle);

            // Draw player name
            // Set text color and font
            gc.setFill(i == 0 ? Color.GOLD : Color.WHITE);
            gc.setFont(new Font("Arial", 18));
            gc.fillText(player[0], playerX - 30, playerY - 10);

            // Draw hole cards
            drawCard(gc, player[1], playerX - 30, playerY + 10);
            drawCard(gc, player[2], playerX + 20, playerY + 10);
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
    private static void drawCard(GraphicsContext gc, String card, double x, double y) {
        // Draw card background
        gc.setFill(Color.WHITE);
        gc.fillRoundRect(x, y, 40, 60, 10, 10);

        // Draw card border
        gc.setStroke(Color.BLACK);
        gc.strokeRoundRect(x, y, 40, 60, 10, 10);

        // Draw card text
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 14));
        gc.fillText(toCardLabel(card), x + 8, y + 30);
    }

    private static String toCardLabel(String card) {
        return card
                .replace("h", "❤️")
                .replace("d", "♦️")
                .replace("c", "♣️")
                .replace("s", "♠️");
    }
}
