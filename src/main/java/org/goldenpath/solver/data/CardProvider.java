package org.goldenpath.solver.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardProvider {
    private static final Character[] SUITS = new Character[]{'h', 'd', 'c', 's'};
    private static final Character[] RANKS = new Character[]{'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'};

    public static String[] getDeck(List<String> exclude) {
        var deck = new String[52 - exclude.size()];
        int i = 0;
        for (var suit : SUITS) {
            for (var rank : RANKS) {
                var card = new String(new char[]{rank, suit});
                if (!exclude.contains(card)) {
                    deck[i] = card;
                    i++;
                }
            }
        }

        return deck;
    }

    public static String[] getDeck(String[] exclude) {
        return getDeck(Arrays.asList(exclude));
    }
}
