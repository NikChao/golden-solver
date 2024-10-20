package org.goldenpath.solver.compute.model;

import java.util.ArrayList;

public class GameTree {
    public record Action(String name, int amount) {
    }

    public int street;
    public GameTree[] children;
    public Action action;

    public GameTree(CrmInput input) {
        new GameTree(input, 1, 0, 0);
    }

    /**
     * @param input
     * @param street 0: Pre-flop (not supported), 1: Flop, 2: Turn, 3: River
     * @param actor  0: OutOfPosition, 1: InPosition - Today we only support heads up
     */
    public GameTree(CrmInput input, int street, int actor, int betToMatch) {
        this.street = street;
        if (street > 3) {
            return;
        }

        var nextActor = actor == input.players().length - 1 ? 0 : actor + 1;

        // TODO: support players left to act
        var nextStreet = actor == input.players().length - 1 ? street + 1 : street;

        var playerToAct = input.players()[actor];
        var betAndRaiseSizes = betAndRaiseSizes(playerToAct, street);
        var children = new ArrayList<GameTree>();

        // Fold
        var foldNode = new GameTree(
                input,
                nextStreet,
                nextActor,
                betToMatch
        );
        foldNode.action = new Action("fold", 0);
        children.add(foldNode);

        // Check
        var checkNode = new GameTree(
                input,
                nextStreet,
                nextActor,
                betToMatch
        );
        checkNode.action = new Action("check", 0);
        children.add(checkNode);


        if (betToMatch > 0) {
            var raiseSizes = betAndRaiseSizes[1];
            for (var raiseSize : raiseSizes) {
                var raise = Math.toIntExact(Math.round(raiseSize * input.pot()));
                if (raise > input.allinThreshold() * playerToAct.stack()) {
                    raise = playerToAct.stack();
                }

                new Action("raise", raise);
                var nextInput = nextInput(input, action);
                var node = new GameTree(
                        nextInput,
                        nextStreet,
                        nextActor,
                        raise
                );
                node.action = action;
                children.add(node);
            }
        } else {
            var betSizes = betAndRaiseSizes[0];
            for (var betSize : betSizes) {
                var bet = Math.toIntExact(Math.round(betSize * input.pot()));
                children.add(new GameTree(input, street, nextActor, bet));
            }
        }
    }


    private static double[][] betAndRaiseSizes(PlayerParams player, int street) {
        switch (street) {
            case 1:
                return new double[][]{player.flopBetSizes(), player.flopRaiseSizes()};
            case 2:
                return new double[][]{player.turnBetSizes(), player.turnRaiseSizes()};
            case 3:
                return new double[][]{player.riverBetSizes(), player.riverRaiseSizes()};
            default:
                return new double[][]{};
        }
    }

    private static CrmInput nextInput(CrmInput previosInput, Action action) {
        return new CrmInput(
                previosInput.players(),
                previosInput.raiseLimit(),
                previosInput.pot() + action.amount,
                previosInput.effectiveStack(),
                previosInput.allinThreshold(),
                previosInput.boardCards()
        );
    }
}
