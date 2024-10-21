package org.goldenpath.solver.compute;

import java.util.*;

public class EquityCalculator {
    private static final Character[] suits = new Character[]{'h', 'd', 'c', 's'};
    private static final Character[] ranks = new Character[]{'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'};

    private final HandResolver handResolver;

    public EquityCalculator(HandResolver handResolver) {
        this.handResolver = handResolver;
    }

    public double[] handVsHand(String[] heroHand, String[] villainHand, String[] board) {
        var cardsToExclude = new ArrayList<String>();
        Collections.addAll(cardsToExclude, board);
        Collections.addAll(cardsToExclude, heroHand);
        Collections.addAll(cardsToExclude, villainHand);
        var cards = allCards(cardsToExclude);

        var wins = handVsHandWins(heroHand, villainHand, board, cards);

        var heroWins = wins[0];
        var villainWins = wins[1];
        var chops = wins[2];

        var totalOutcomes = heroWins + villainWins + chops;
        var heroEquity = heroWins / totalOutcomes;
        var villainEquity = villainWins / totalOutcomes;
        var chopChance = chops / totalOutcomes;
        return new double[]{heroEquity, villainEquity, chopChance};
    }

    public double[] handVsRange(String[] hand, String[][] range, String[] board) {
        var heroWins = 0d;
        var villainWins = 0d;
        var chops = 0d;

        for (var villainHand : range) {
            var handIsInvalid = hand[0].equals(villainHand[0])
                    || hand[1].equals(villainHand[1])
                    || hand[1].equals(villainHand[0])
                    || hand[0].equals(villainHand[1]);
            for (var boardCard : board) {
                if (villainHand[0].equals(boardCard) || villainHand[1].equals(boardCard)) {
                    handIsInvalid = true;
                }
            }
            if (handIsInvalid) {
                // Villain can't have the same hand as hero
                continue;
            }

            var cardsToExclude = new ArrayList<String>();
            Collections.addAll(cardsToExclude, board);
            Collections.addAll(cardsToExclude, hand);
            Collections.addAll(cardsToExclude, villainHand);
            var cards = allCards(cardsToExclude);

            var result = handVsHandWins(hand, villainHand, board, cards);
            heroWins += result[0];
            villainWins += result[1];
            chops += result[2];
        }

        var totalOutcomes = heroWins + villainWins + chops;
        var heroEquity = heroWins / totalOutcomes;
        var villainEquity = villainWins / totalOutcomes;
        var chopChance = chops / totalOutcomes;
        return new double[]{heroEquity, villainEquity, chopChance};
    }

    private double[] handVsHandWins(String[] heroHand, String[] villainHand, String[] board, List<String> cards) {
        double heroWins = 0;
        double villainWins = 0;
        double chops = 0;

        if (board.length == 0) {
            var flops = generateFlops(cards.toArray(new String[cards.size()]));
            for (var flop : flops) {
                var newCards = cards.stream().filter(card -> !contains(flop, card)).toList();
                var result = handVsHandWins(heroHand, villainHand, flop, newCards);
                heroWins += result[0];
                villainWins += result[1];
                chops += result[2];
            }
        } else if (board.length == 3 || board.length == 4) {
            for (var card : cards) {
                var boardWithNewCard = new String[board.length + 1];
                for (int i = 0; i < board.length; i++) boardWithNewCard[i] = board[i];
                boardWithNewCard[board.length] = card;

                var result = handVsHandWins(heroHand, villainHand, boardWithNewCard, cards.stream().filter(x -> !x.equals(card)).toList());
                heroWins += result[0];
                villainWins += result[1];
                chops += result[2];
            }
        } else if (board.length == 5) {
            var outcome = handResolver.compareHands(heroHand, villainHand, board);
            if (outcome == HandResolver.Player.OutOfPosition) return new double[]{1, 0, 0};
            if (outcome == HandResolver.Player.InPosition) return new double[]{0, 1, 0};
            if (outcome == HandResolver.Player.Chop) return new double[]{0, 0, 1};
        } else {
            throw new Error("Invalid board of length: " + board.length);
        }

        return new double[]{heroWins, villainWins, chops};
    }

    private static List<String> allCards(List<String> cardsToExclude) {
        var cards = new ArrayList<String>();
        for (var suit : suits) {
            for (var rank : ranks) {
                var card = new String(new char[]{rank, suit});
                if (!cardsToExclude.contains(card)) {
                    cards.add(card);
                }
            }
        }

        return cards;
    }

    private static <T> boolean contains(final T[] array, final T v) {
        for (final T e : array)
            if (e == v || v != null && v.equals(e))
                return true;

        return false;
    }

    private static String[][] generateFlops(String[] cards) {
        var result = new HashSet<String>();

        var processed = 0;

        for (int i = 0; i < cards.length; i++) {
            for (int j = i; j < cards.length; j++) {
                for (int k = j; k < cards.length; k++) {
                    if (i != j && i != k && j != k) {
                        result.add(cards[i] + "," + cards[j] + "," + cards[k]);
                    }
                }
            }
        }

        var combos = Arrays.stream(result.toArray(new String[result.size()])).map(c -> c.split(","));

        return combos.toArray(String[][]::new);
    }
}
