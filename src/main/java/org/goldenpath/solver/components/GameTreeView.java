package org.goldenpath.solver.components;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.*;
import org.goldenpath.solver.compute.RangeProvider;
import org.goldenpath.solver.compute.model.GameTree;

import java.util.stream.Stream;

public class GameTreeView {
    private final GameTree gameTree;

    public GameTreeView(GameTree gameTree) {
        this.gameTree = gameTree;
    }

    public Pane render() {
        var layout = new BorderPane();
        var grid = new RangeGrid("Strategy", new RangeProvider(0), 0).getRangeGrid(false);

        var rootItem = toTreeItem(gameTree);
        var treeView = new TreeView<>(rootItem);
        treeView.setMinWidth(600);
        treeView.setMinHeight(800);

        layout.setLeft(treeView);
        layout.setRight(grid);

        return layout;
    }

    private static TreeItem<String> toTreeItem(GameTree gameTree) {
        if (gameTree == null) {
            return new TreeItem<>("END");
        }

        var rootItem = new TreeItem<>(toLabel(gameTree));

        if (gameTree.children == null) {
            return rootItem;
        }

        for (var child : gameTree.children) {
            var item = toTreeItem(child);
            rootItem.getChildren().add(item);
        }

        return rootItem;
    }

    private static String toLabel(GameTree gameTree) {
        switch (gameTree.action.type()) {
            case BET:
            case RAISE:
                return gameTree.action.type() + " " + gameTree.action.amount();
            case CARD:
                return gameTree.street + " " + gameTree.action.card();
            case CALL:
            case START_STREET:
            case FOLD:
            case CHECK:
            default:
                return gameTree.action.type().name();
        }
    }
}
