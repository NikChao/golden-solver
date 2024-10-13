package org.goldenpath.solver;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.goldenpath.solver.components.*;
import org.goldenpath.solver.compute.CrmSolver;
import org.goldenpath.solver.compute.HandResolver;
import org.goldenpath.solver.compute.RangeProvider;
import org.goldenpath.solver.data.CrmInputConverter;

import java.lang.reflect.Method;

public class Solver extends Application {
    private final HandResolver handResolver = new HandResolver();
    private final CrmInputConverter crmInputConverter = new CrmInputConverter();

    private final double initialOopRange = 30;
    private final double initialIpRange = 50;

    private final RangeProvider oopRanges = new RangeProvider(initialOopRange);
    private final RangeProvider ipRanges = new RangeProvider(initialIpRange);

    private final RangeGrid oopRangeGridProvider = new RangeGrid("Out of position", oopRanges, initialOopRange);
    private final RangeGrid ipRangeGridProvider = new RangeGrid("In position", ipRanges, initialIpRange);

    @Override
    public void start(Stage primaryStage) {
        var oopRangeGrid = oopRangeGridProvider.getRangeGrid();
        var ipRangeGrid = ipRangeGridProvider.getRangeGrid();

        var rangeGrids = new HBox();
        var rangeSpacer = new Region();
        rangeSpacer.setMinWidth(20);
        rangeGrids.getChildren().addAll(oopRangeGrid, rangeSpacer, ipRangeGrid);

        var icon = new Image(getClass().getResourceAsStream("/images/logo.jpeg"));
        var board = new Board();

        var ipParams = new PositionParams("In position");
        var oopParams = new PositionParams("Out of position");
        var title = new Title();
        var otherParams = new OtherParams();

        var solveButton = new Button("Find solution");
        solveButton.setStyle("-fx-start-margin: 24px;");
        solveButton.setOnMouseClicked((MouseEvent event) -> {
            if (board.getBoard().isBlank()) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error solving");
                alert.setContentText("Please input board before continuing.");
                alert.show();
                return;
            }
            var solver = new CrmSolver(new HandResolver());
            var solverInput = crmInputConverter.toCrmInput(oopRanges, oopParams, ipRanges, ipParams, otherParams, board);
            var result = solver.solve(solverInput);
        });

        var vbox = new VBox(24); // 10px spacing between elements
        vbox.setPadding(new Insets(10, 10, 10, 10));

        vbox.getChildren().addAll(
                title.render(),
                rangeGrids,
                board.render(),
                ipParams.render(),
                oopParams.render(),
                otherParams.render(),
                solveButton);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        var scene = new Scene(scrollPane, 300, 200);

        // Set up the stage (window)
        primaryStage.setTitle("Solver");
        primaryStage.setScene(scene);
        primaryStage.setHeight(840);
        primaryStage.setWidth(1300);
        setDockIcon(icon);
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void setDockIcon(Image image) {
        try {
            Class util = Class.forName("com.apple.eawt.Application");
            Method getApplication = util.getMethod("getApplication", new Class[0]);
            Object application = getApplication.invoke(util);
            Class params[] = new Class[1];
            params[0] = Image.class;
            Method setDockIconImage = util.getMethod("setDockIconImage", params);
            setDockIconImage.invoke(application, image);
        } catch (Exception e) {
            System.err.println("Could not set dock image: " + e.getStackTrace());
        }
    }
}
