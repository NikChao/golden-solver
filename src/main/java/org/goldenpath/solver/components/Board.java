package org.goldenpath.solver.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Board {
    private final TextField boardTextArea;
    private final Pane board;

    public Board() {
        var board = new VBox(10);
        var boardLabel = new Label("Board");
        boardTextArea = new TextField();
        boardTextArea.setMinWidth(280);
        boardTextArea.setPromptText("Board cards (i.e. Qs,5d,8c");
        var selectBoardCards = new Button("Select board cards");
        var boardInputAndButtonContainer = new HBox(10);
        boardInputAndButtonContainer.getChildren().addAll(boardTextArea, selectBoardCards);
        board.getChildren().addAll(boardLabel, boardInputAndButtonContainer);
        this.board = board;
    }

    public Pane render() {
        return board;
    }

    public String getBoard() {
        return boardTextArea.getText();
    }
}
