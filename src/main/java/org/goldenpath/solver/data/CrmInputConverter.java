package org.goldenpath.solver.data;

import org.goldenpath.solver.components.Board;
import org.goldenpath.solver.components.OtherParams;
import org.goldenpath.solver.components.PositionParams;
import org.goldenpath.solver.compute.model.CrmInput;
import org.goldenpath.solver.compute.RangeProvider;
import org.goldenpath.solver.compute.model.PlayerParams;

import java.util.ArrayList;

public class CrmInputConverter {
    private static final char[] SUITS = new char[]{'h', 'd', 'c', 's'};

    public CrmInput toCrmInput(
            RangeProvider oopRange,
            PositionParams oopParams,
            RangeProvider ipRange,
            PositionParams ipParams,
            OtherParams otherParams,
            Board board
    ) {
        var oop = fromPositionParams(oopParams, oopRange);
        var ip = fromPositionParams(ipParams, ipRange);
        var players = new PlayerParams[]{oop, ip};

        return new CrmInput(
                players,
                Integer.parseInt(otherParams.getValues().raiseLimit()),
                Integer.parseInt(otherParams.getValues().pot()),
                Integer.parseInt(otherParams.getValues().effectiveStack()),
                Double.parseDouble(otherParams.getValues().allinThreshold()),
                board.getBoard()
        );
    }

    private static PlayerParams fromPositionParams(PositionParams positionParams, RangeProvider rangeProvider) {
        return new PlayerParams(
                toSuitedRange(rangeProvider.range),
                sizesFromText(positionParams.getValues().flop().bet()),
                sizesFromText(positionParams.getValues().flop().raise()),
                sizesFromText(positionParams.getValues().turn().bet()),
                sizesFromText(positionParams.getValues().turn().raise()),
                sizesFromText(positionParams.getValues().river().bet()),
                sizesFromText(positionParams.getValues().river().raise())
        );
    }

    private static double[] sizesFromText(String input) {
        var sizesText = input.strip().split(",");
        var sizes = new double[sizesText.length];
        for (int i = 0; i < sizesText.length; i++) {
            sizes[i] = Double.parseDouble(sizesText[i]);
        }

        return sizes;
    }

    /**
     * I.e.
     * "AKs" -> "AhKh,AdKd,AcKc,AsKs"
     * "AK"  -> "AhKh,AhKd,AhKc,AhKs,AdKh,AdKd,AdKc,AdKs,AcKh,AcKd,AcKc,AcKs,AsKh,AsKd,AsKc,AsKs
     * "AA"  -> "AhAd,AhAc,AhAs,AdAc,AdAs,AcAs"
     */
    private static String[] toSuitedRange(String range) {
        var unfoldedHands = new ArrayList<String>();
        var hands = range.split(",");

        for (var hand : hands) {
            var firstCard = hand.charAt(0);
            var secondCard = hand.charAt(1);

            if (hand.endsWith("s")) {
                // suited
                for (char suit : SUITS) {
                    unfoldedHands.add(new StringBuilder().append(firstCard).append(suit).append(secondCard).append(suit).toString());
                }
            } else if (firstCard == secondCard) {
                // pair
                for (int i = 0; i < 4; i++) {
                    var suit = SUITS[i];
                    for (int j = 0; j < 4; j++) {
                        var suit2 = SUITS[j];
                        // Once we've done AhAd we don't want to repeat with AdAh
                        // We also can't have AhAh
                        if (j > i && suit != suit2) {
                            unfoldedHands.add(new StringBuilder().append(firstCard).append(suit).append(secondCard).append(suit2).toString());
                        }
                    }
                }
            } else {
                for (char suit : SUITS) {
                    for (char suit2 : SUITS) {
                        unfoldedHands.add(new StringBuilder().append(firstCard).append(suit).append(secondCard).append(suit2).toString());
                    }
                }
            }
        }

        return unfoldedHands.toArray(new String[0]);
    }
}
