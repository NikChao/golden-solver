package org.goldenpath.solver;

import atlantafx.base.theme.Dracula;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.goldenpath.solver.components.*;
import org.goldenpath.solver.compute.CrmSolver;
import org.goldenpath.solver.compute.EquityCalculator;
import org.goldenpath.solver.compute.HandResolver;
import org.goldenpath.solver.compute.RangeProvider;
import org.goldenpath.solver.compute.model.GameTree;
import org.goldenpath.solver.data.CrmInputConverter;
import org.goldenpath.solver.data.RangeConverter;
import org.goldenpath.solver.utils.AppIcon;

public class Solver extends Application {
    private final RangeConverter rangeConverter = new RangeConverter();
    private final HandResolver handResolver = new HandResolver();
    private final CrmInputConverter crmInputConverter = new CrmInputConverter(rangeConverter);
    private final AppIcon appIcon = new AppIcon();

    private final double initialOopRange = 30;
    private final double initialIpRange = 50;

    private final RangeProvider oopRanges = new RangeProvider(initialOopRange);
    private final RangeProvider ipRanges = new RangeProvider(initialIpRange);

    private final RangeGrid oopRangeGridProvider = new RangeGrid("Out of position", oopRanges, initialOopRange);
    private final RangeGrid ipRangeGridProvider = new RangeGrid("In position", ipRanges, initialIpRange);
    private final Practice practice = new Practice();

    @Override
    public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());

        var tabPane = new TabPane();

        var oopRangeGrid = oopRangeGridProvider.getRangeGrid();
        var ipRangeGrid = ipRangeGridProvider.getRangeGrid();

        var rangeGrids = new HBox();
        var rangeSpacer = new Region();
        rangeSpacer.setMinWidth(20);
        rangeGrids.getChildren().addAll(oopRangeGrid, rangeSpacer, ipRangeGrid);

        var icon = new Image(getClass().getResourceAsStream("/logo.jpeg"));
        var board = new Board();

        var ipParams = new PositionParams("In position");
        var oopParams = new PositionParams("Out of position");
        var title = new Title();
        var otherParams = new OtherParams();

        var solutionPane = new VBox();
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
            var solverInput = crmInputConverter.toCrmInput(oopRanges, oopParams, ipRanges, ipParams, otherParams, board);
            var gameTree = new GameTree(solverInput);
            var solver = new CrmSolver(handResolver, solverInput, gameTree);
            var result = solver.solve();

            var solutionView = new SolutionView(gameTree);
            solutionPane.getChildren().setAll(solutionView.render());
        });

        var vbox = new VBox(24);
        vbox.setPadding(new Insets(16, 16, 16, 16));

        vbox.getChildren().addAll(
                title.render(),
                rangeGrids,
                board.render(),
                ipParams.render(),
                oopParams.render(),
                otherParams.render(),
                solveButton);

        ScrollPane solverScrollPane = new ScrollPane();
        solverScrollPane.setContent(vbox);
        solverScrollPane.setFitToWidth(true);
        solverScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        solverScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        var solverTabPane = new TabPane();
        solverTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        var solverInputTab = new Tab("Input", solverScrollPane);
        var solverSolutionTab = new Tab("Solution", solutionPane);
        solverTabPane.getTabs().addAll(solverInputTab, solverSolutionTab);

        var equityCalculator = new EquityCalculator(handResolver);
        var villainRangeProvider = new RangeProvider();
        var villainRangeGrid = new RangeGrid("Villain Range", villainRangeProvider, 0.5);
        var equityLab = new EquityLab(equityCalculator, villainRangeGrid, villainRangeProvider, rangeConverter);

        var equityTab = new Tab("Equity", equityLab.render());
        var solverTab = new Tab("Solver", solverTabPane);
        tabPane.getTabs().addAll(equityTab, solverTab, practice.tab());
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        var scene = new Scene(tabPane, 300, 200);

        appIcon.setAppIcon(icon, primaryStage);
        primaryStage.setTitle("Solver");
        primaryStage.setScene(scene);
        primaryStage.setHeight(840);
        primaryStage.setWidth(1300);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
