package org.goldenpath.solver.compute.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTreeTest {
    private static final String TIGHT_RANGE_TEXT = "AhAd,AhAc,AhAs,AdAc,AdAs,AcAs,KhKd,KhKc,KhKs,KdKc,KdKs,KcKs," +
            "QhQd,QhQc,QhQs,QdQc,QdQs,QcQs,AhKh,AdKd,AcKc,AsKs,JhJd,JhJc,JhJs,JdJc,JdJs,JcJs,AhQh,AdQd,AcQc," +
            "AsQs,KhQh,KdQd,KcQc,KsQs,AhJh,AdJd,AcJc,AsJs,KhJh,KdJd,KcJc,KsJs,ThTd,ThTc,ThTs,TdTc,TdTs," +
            "TcTs,AhKh,AhKd,AhKc,AhKs,AdKh,AdKd,AdKc,AdKs,AcKh,AcKd,AcKc,AcKs,AsKh,AsKd,AsKc,AsKs,AhTh," +
            "AdTd,AcTc,AsTs,QhJh,QdJd,QcJc,QsJs,KhTh,KdTd,KcTc,KsTs,QhTh,QdTd,QcTc,QsTs,JhTh,JdTd,JcTc,JsTs,9h9d,9h9c," +
            "9h9s,9d9c,9d9s,9c9s";
    private static final String[] TIGHT_RANGE = TIGHT_RANGE_TEXT.split(",");

    @Test
    public void testGameTreeCanCheckThrough() {
        var oop = new PlayerParams(TIGHT_RANGE, 200, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5});
        var ip = new PlayerParams(TIGHT_RANGE, 200, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5});
        var players = new PlayerParams[]{oop, ip};
        var boardCards = new String[]{"Qh", "5c", "8d"};

        var input = new CrmInput(players, 3, 50, 200, 0.67, boardCards);

        var gameTree = new GameTree(input);

        assertEquals(gameTree.action.type(), GameTree.ActionType.START_STREET);
        assertEquals(gameTree.street, GameTree.Street.FLOP);
        assertEquals(gameTree.children.length, 3);

        var oopCheckFlop = gameTree.children[0];
        assertEquals(GameTree.ActionType.CHECK, oopCheckFlop.action.type());

        var ipCheckFlop = oopCheckFlop.children[0];
        assertEquals(GameTree.ActionType.CHECK, ipCheckFlop.action.type());

        var turnCard = ipCheckFlop.children[0];
        assertEquals(GameTree.ActionType.CARD, turnCard.action.type());

        var startTurn = turnCard.children[0];
        assertEquals(GameTree.ActionType.START_STREET, startTurn.action.type());

        var oopCheckTurn = startTurn.children[0];
        assertEquals(GameTree.ActionType.CHECK, oopCheckTurn.action.type());

        var ipCheckTurn = oopCheckTurn.children[0];
        assertEquals(GameTree.ActionType.CHECK, ipCheckTurn.action.type());

        var river = ipCheckTurn.children[0];
        assertEquals(GameTree.ActionType.CARD, river.action.type());

        var startRiver = river.children[0];
        assertEquals(GameTree.ActionType.START_STREET, startRiver.action.type());

        var oopCheckRiver = startRiver.children[0];
        assertEquals(GameTree.ActionType.CHECK, oopCheckRiver.action.type());

        var ipCheckRiver = oopCheckRiver.children[0];
        assertEquals(GameTree.ActionType.CHECK, ipCheckRiver.action.type());

        // After checking back the river there should be no more game to play
        assertNull(ipCheckRiver.children);
    }

    @Test
    public void testGameTreeCallStationPot() {
        var oop = new PlayerParams(TIGHT_RANGE, 200, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5});
        var ip = new PlayerParams(TIGHT_RANGE, 200, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5});
        var players = new PlayerParams[]{oop, ip};
        var boardCards = new String[]{"Qh", "5c", "8d"};

        var input = new CrmInput(players, 3, 50, 200, 0.67, boardCards);

        var gameTree = new GameTree(input);

        assertEquals(gameTree.action.type(), GameTree.ActionType.START_STREET);
        assertEquals(gameTree.street, GameTree.Street.FLOP);
        assertEquals(gameTree.children.length, 3);
        assertEquals(gameTree.pot, 50);

        var oopBetFlop = gameTree.children[1];
        assertEquals(GameTree.ActionType.BET, oopBetFlop.action.type());
        assertEquals(oopBetFlop.pot, 65);

        var ipCallFlop = oopBetFlop.children[1];
        assertEquals(GameTree.ActionType.CALL, ipCallFlop.action.type());
        assertEquals(ipCallFlop.pot, 80);

        var turnCard = ipCallFlop.children[0];
        assertEquals(GameTree.ActionType.CARD, turnCard.action.type());

        var startTurn = turnCard.children[0];
        assertEquals(GameTree.ActionType.START_STREET, startTurn.action.type());

        var oopBetTurn = startTurn.children[1];
        assertEquals(GameTree.ActionType.BET, oopBetTurn.action.type());
        assertEquals(oopBetTurn.pot, 104);

        var ipCallTurn = oopBetTurn.children[1];
        assertEquals(GameTree.ActionType.CALL, ipCallTurn.action.type());
        assertEquals(ipCallTurn.pot, 128);

        var river = ipCallTurn.children[0];
        assertEquals(GameTree.ActionType.CARD, river.action.type());

        var startRiver = river.children[0];
        assertEquals(GameTree.ActionType.START_STREET, startRiver.action.type());

        var oopBetRiver = startRiver.children[1];
        assertEquals(GameTree.ActionType.BET, oopBetRiver.action.type());
        assertEquals(oopBetRiver.pot, 166);

        var ipCallRiver = oopBetRiver.children[1];
        assertEquals(GameTree.ActionType.CALL, ipCallRiver.action.type());
        assertEquals(ipCallRiver.pot, 204);
    }

    @Test
    public void testGameTreeFourBetLimitWhenRaisingFlop() {
        var oop = new PlayerParams(TIGHT_RANGE, 200, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5});
        var ip = new PlayerParams(TIGHT_RANGE, 200, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5}, new double[]{0.3, 0.6}, new double[]{0.5, 1.5});
        var players = new PlayerParams[]{oop, ip};
        var boardCards = new String[]{"Qh", "5c", "8d"};

        var input = new CrmInput(players, 3, 50, 200, 0.67, boardCards);

        var gameTree = new GameTree(input);

        assertEquals(gameTree.action.type(), GameTree.ActionType.START_STREET);
        assertEquals(gameTree.street, GameTree.Street.FLOP);
        assertEquals(gameTree.children.length, 3);
        assertEquals(gameTree.pot, 50);

        var oopBetFlop = gameTree.children[1];
        assertEquals(GameTree.ActionType.BET, oopBetFlop.action.type());
        assertEquals(oopBetFlop.pot, 65);

        var ipRaiseFlop = oopBetFlop.children[2];
        assertEquals(GameTree.ActionType.RAISE, ipRaiseFlop.action.type());
        assertEquals(ipRaiseFlop.pot, 98);

        var oopReRaiseFlop = ipRaiseFlop.children[2];
        assertEquals(GameTree.ActionType.RAISE, oopReRaiseFlop.action.type());
        assertEquals(oopReRaiseFlop.pot, 147);

        var ipThreeBetFlop = oopReRaiseFlop.children[2];
        assertEquals(GameTree.ActionType.RAISE, ipThreeBetFlop.action.type());
        assertEquals(ipThreeBetFlop.pot, 221);

        var oopFourBetPot = ipThreeBetFlop.children[2];
        assertEquals(GameTree.ActionType.RAISE, oopFourBetPot.action.type());
        assertEquals(oopFourBetPot.pot, 357);

        // End of re-raise limit
        assertEquals(oopFourBetPot.children.length, 2);
        assertEquals(oopFourBetPot.children[0].action.type(), GameTree.ActionType.FOLD);
        assertEquals(oopFourBetPot.children[1].action.type(), GameTree.ActionType.CALL);
    }
}
