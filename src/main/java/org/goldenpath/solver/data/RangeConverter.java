package org.goldenpath.solver.data;

import java.util.ArrayList;

public class RangeConverter {
    private static final char[] SUITS = new char[]{'h', 'd', 'c', 's'};
    
    /**
     * I.e.
     * "AKs" -> "AhKh,AdKd,AcKc,AsKs"
     * "AK"  -> "AhKh,AhKd,AhKc,AhKs,AdKh,AdKd,AdKc,AdKs,AcKh,AcKd,AcKc,AcKs,AsKh,AsKd,AsKc,AsKs
     * "AA"  -> "AhAd,AhAc,AhAs,AdAc,AdAs,AcAs"
     */
    public String[] toSuitedRange(String range) {
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
