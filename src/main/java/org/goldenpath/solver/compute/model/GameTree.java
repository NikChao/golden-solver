package org.goldenpath.solver.compute.model;

import java.util.ArrayList;
import java.util.Map;

public class GameTree {
    public enum Street {
        PREFLOP,
        FLOP,
        TURN,
        RIVER
    }

    public enum ActionType {
        START,
        CHECK,
        BET,
        RAISE,
        CALL,
        FOLD,
        CARD;
    }

    public record Action(ActionType type, double amount, String card) {
    }

    private static final Map<Integer, Street> STREET_MAP = Map.of(
            0, Street.PREFLOP,
            3, Street.FLOP,
            4, Street.TURN,
            5, Street.RIVER
    );

    public final double pot;
    public final double betToMatch = 0;
    public int playerToAct = 0;
    public final Action action;
    public GameTree[] children;
    public String[] board;
    public Street street = Street.PREFLOP;

    public GameTree(CrmInput input) {
        this.action = new Action(ActionType.START, 0, null);
        this.pot = input.pot();
        this.board = input.boardCards();
        this.street = STREET_MAP.getOrDefault(board.length, Street.PREFLOP);

        var playerActions = getActions(input);

        children = playerActions.stream().map(action -> new GameTree(input, this, action)).toArray(GameTree[]::new);
    }

    public GameTree(CrmInput input, GameTree parent, Action action) {
        this.action = action;
        this.pot = parent.pot + action.amount;
        this.street = toStreet(parent, action);
    }

    private ArrayList<Action> getActions(CrmInput input) {
        var playerActions = new ArrayList<Action>();
        var player = input.players()[playerToAct];

        if (betToMatch > 0) {
            playerActions.add(new Action(ActionType.FOLD, 0, null));
            playerActions.add(new Action(ActionType.CALL, betToMatch, null));
            playerActions.add(new Action(ActionType.RAISE, betToMatch * 2, null));
        } else {
            playerActions.add(new Action(ActionType.CHECK, 0, null));

            for (var betSize : getBetSizes(player, street)) {
                var absoluteBet = Math.round(betSize * pot);
                var allinThreshold = player.stack() * input.allinThreshold();
                if (absoluteBet >= allinThreshold) {
                    playerActions.add(new Action(ActionType.BET, player.stack(), null));
                } else {
                    playerActions.add(new Action(ActionType.BET, absoluteBet, null));
                }
            }
        }
        return playerActions;
    }

    private double[] getBetSizes(PlayerParams player, Street street) {
        switch (street) {
            case FLOP:
                return player.flopBetSizes();
            case TURN:
                return player.turnBetSizes();
            case RIVER:
                return player.riverBetSizes();
            case PREFLOP:
            default:
                return new double[]{};
        }
    }

    private static Street toStreet(GameTree parent, Action action) {
        return parent.street;
    }
}
