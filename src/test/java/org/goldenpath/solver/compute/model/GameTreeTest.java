package org.goldenpath.solver.compute.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameTreeTest {
    private static final String TIGHT_RANGE_TEXT = "AhAd,AhAc,AhAs,AdAc,AdAs,AcAs,KhKd,KhKc,KhKs,KdKc,KdKs,KcKs," +
            "QhQd,QhQc,QhQs,QdQc,QdQs,QcQs,AhKh,AdKd,AcKc,AsKs,JhJd,JhJc,JhJs,JdJc,JdJs,JcJs,AhQh,AdQd,AcQc," +
            "AsQs,KhQh,KdQd,KcQc,KsQs,AhJh,AdJd,AcJc,AsJs,KhJh,KdJd,KcJc,KsJs,ThTd,ThTc,ThTs,TdTc,TdTs," +
            "TcTs,AhKh,AhKd,AhKc,AhKs,AdKh,AdKd,AdKc,AdKs,AcKh,AcKd,AcKc,AcKs,AsKh,AsKd,AsKc,AsKs,AhTh," +
            "AdTd,AcTc,AsTs,QhJh,QdJd,QcJc,QsJs,KhTh,KdTd,KcTc,KsTs,QhTh,QdTd,QcTc,QsTs,JhTh,JdTd,JcTc,JsTs,9h9d,9h9c," +
            "9h9s,9d9c,9d9s,9c9s";
    private static final String[] TIGHT_RANGE = TIGHT_RANGE_TEXT.split(",");

    @Test
    public void testGameTree() {
        var oop = new PlayerParams(TIGHT_RANGE, 200, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5});
        var ip = new PlayerParams(TIGHT_RANGE, 200, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5});
        var players = new PlayerParams[]{oop, ip};
        var boardCards = new String[]{"Qh", "5c", "8d"};

        var input = new CrmInput(players, 3, 50, 200, 0.67, boardCards);

        var gameTree = new GameTree(input);

        assertEquals(gameTree.action.type(), GameTree.ActionType.START);
        assertEquals(gameTree.street, GameTree.Street.FLOP);
        assertEquals(gameTree.children.length, 3);
    }
}
