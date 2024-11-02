package org.goldenpath.solver.compute.model;

import org.goldenpath.solver.data.CardProvider;

import java.util.*;

/**
 * Game tree describing all the checks, bets, calls, raises and folds that can happen in a game
 * TODO: support custom turn and river cards
 */
public class GameTree {
    public enum Street {
        PREFLOP,
        FLOP,
        TURN,
        RIVER
    }

    public enum ActionType {
        START_STREET,
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

    public GameTree parent;
    public double pot;
    public double betToMatch = 0;
    public int playerToAct = 0;
    public Action action;
    public GameTree[] children;
    public String[] board;
    public Street street = Street.PREFLOP;
    public int lastPlayerToAct;

    public GameTree(CrmInput input) {
        this.action = new Action(ActionType.START_STREET, 0, null);
        this.pot = input.pot();
        this.board = input.boardCards();
        this.street = STREET_MAP.getOrDefault(board.length, Street.PREFLOP);
        this.lastPlayerToAct = input.players().length - 1;
        this.parent = null;

        var playerActions = getActions(input);

        children = playerActions.stream().map(action -> new GameTree(
                toNewInput(input, action),
                nextPlayerToAct(input),
                getLastPlayerToAct(input, action),
                this,
                action)).toArray(GameTree[]::new);
    }

    public GameTree(CrmInput input, int playerToAct, int lastPlayerToAct, GameTree parent, Action action) {
        this.action = action;
        this.pot = input.pot();
        this.street = toStreet(parent, action);
        this.betToMatch = action.amount;
        this.playerToAct = playerToAct;
        this.lastPlayerToAct = lastPlayerToAct;
        this.parent = parent;

        if (input.players()[playerToAct].stack() <= 0) return;

        if (parent.lastPlayerToAct == parent.playerToAct && List.of(ActionType.CHECK, ActionType.CALL).contains(action.type)) {
            if (!street.RIVER.equals(street)) {
                var deck = CardProvider.getDeck(input.boardCards());

                children = new GameTree[deck.length];

                for (int i = 0; i < deck.length; i++) {
                    children[i] = new GameTree(input, this, deck[i]);
                }

            }
            return;
        }
        if (List.of(ActionType.FOLD).contains(action.type)) {
            return;
        }

        var playerActions = getActions(input);
        children = playerActions.stream().map(a -> {
            return new GameTree(toNewInput(input, a), getLastPlayerToAct(input, a), nextPlayerToAct(input), this, a);
        }).toArray(GameTree[]::new);
    }

    public GameTree(CrmInput input, GameTree parent, String card) {
        this.action = new Action(ActionType.CARD, 0, card);
        this.street = parent.nextStreet();
        this.parent = parent;

        var nextInput = toNewInput(input, action);

        children = new GameTree[]{
                new GameTree(nextInput)
        };
    }

    private ArrayList<Action> getActions(CrmInput input) {
        var playerActions = new ArrayList<Action>();
        var player = input.players()[playerToAct];

        if (betToMatch > 0) {
            playerActions.add(new Action(ActionType.FOLD, 0, null));

            // TODO: support all in threshold
            playerActions.add(new Action(ActionType.CALL, betToMatch, null));

            var previousRaiseCount = getPreviousRaiseCount(parent);
            if (previousRaiseCount < input.raiseLimit()) {
                for (var raiseSize : getRaiseSizes(player, street)) {
                    var absoluteBet = Math.round(raiseSize * pot);
                    var allinThreshold = player.stack() * input.allinThreshold();
                    if (absoluteBet >= allinThreshold) {
                        playerActions.add(new Action(ActionType.RAISE, player.stack(), null));
                    } else {
                        playerActions.add(new Action(ActionType.RAISE, absoluteBet, null));
                    }
                }
            }
        } else {
            playerActions.add(new Action(ActionType.CHECK, 0, null));

            var hasAllInned = false;
            for (var betSize : getBetSizes(player, street)) {
                var absoluteBet = Math.round(betSize * pot);
                var allinThreshold = player.stack() * input.allinThreshold();
                if (absoluteBet >= allinThreshold && !hasAllInned) {
                    playerActions.add(new Action(ActionType.BET, player.stack(), null));
                    hasAllInned = true;
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

    private double[] getRaiseSizes(PlayerParams player, Street street) {
        switch (street) {
            case FLOP:
                return player.flopRaiseSizes();
            case TURN:
                return player.turnRaiseSizes();
            case RIVER:
                return player.riverRaiseSizes();
            case PREFLOP:
            default:
                return new double[]{};
        }
    }

    private int nextPlayerToAct(CrmInput input) {
        if (playerToAct == input.players().length - 1) {
            return 0;
        }
        return playerToAct + 1;
    }

    private CrmInput toNewInput(CrmInput input, Action action) {
        var updatedPlayers = new PlayerParams[input.players().length];
        for (int i = 0; i < input.players().length; i++) {
            if (i == playerToAct) {
                var playerToUpdate = input.players()[i];
                var updatedPlayer = new PlayerParams(
                        playerToUpdate.range(),
                        (int) (playerToUpdate.stack() - action.amount),
                        playerToUpdate.flopBetSizes(),
                        playerToUpdate.flopRaiseSizes(),
                        playerToUpdate.turnBetSizes(),
                        playerToUpdate.turnRaiseSizes(),
                        playerToUpdate.riverBetSizes(),
                        playerToUpdate.riverRaiseSizes()
                );
                updatedPlayers[i] = updatedPlayer;
            } else {
                updatedPlayers[i] = input.players()[i];
            }
        }

        var boardList = new ArrayList<String>();
        Collections.addAll(boardList, input.boardCards());
        if (action.card != null) {
            boardList.add(action.card);
        }

        return new CrmInput(
                updatedPlayers,
                input.raiseLimit(),
                (int) (input.pot() + action.amount),
                input.effectiveStack(),
                input.allinThreshold(),
                boardList.toArray(new String[0])
        );
    }

    private int getLastPlayerToAct(CrmInput input, Action action) {
        if (action.type == ActionType.RAISE) {
            if (playerToAct == 0) {
                return input.players().length - 1;
            }

            return playerToAct - 1;
        }

        return lastPlayerToAct;
    }

    private Street nextStreet() {
        switch (street) {
            case PREFLOP:
                return Street.FLOP;
            case FLOP:
                return Street.TURN;
            case TURN:
            default:
                return Street.RIVER;
        }
    }

    private static Street toStreet(GameTree parent, Action action) {
        return parent.street;
    }

    private static int getPreviousRaiseCount(GameTree tree) {
        int i = 0;

        GameTree currentTree = tree;
        while (currentTree != null) {
            if (currentTree.action.type == ActionType.RAISE) {
                i++;
                currentTree = currentTree.parent;
            } else {
                break;
            }
        }

        return i;
    }
}
