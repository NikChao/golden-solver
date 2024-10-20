package org.goldenpath.solver.compute;

import java.util.*;

public class HandResolver {
    public enum Player {
        InPosition,
        OutOfPosition,
        Chop;
    }

    public Player compareHands(String[] oopHand, String[] ipHand, String[] board) {
        // Combine each player's hand with the board to form the 7 cards to evaluate
        String[] oopCombined = combineHandsAndBoard(oopHand, board);
        var oopCombinedValues = new ArrayList<Integer>();
        for (String oopCard : oopCombined) {
            if (oopCard == null || oopCard.length() == 0) {
                continue;
            }

            var rank = oopCard.charAt(0);
            var rankValue = getCardValue(rank);
            oopCombinedValues.add(rankValue);
        }
        oopCombinedValues.sort(Comparator.reverseOrder());

        String[] ipCombined = combineHandsAndBoard(ipHand, board);
        var ipCombinedValues = new ArrayList<Integer>();
        for (String ipCard : ipCombined) {
            if (ipCard == null || ipCard.length() == 0) {
                continue;
            }

            var rank = ipCard.charAt(0);
            var rankValue = getCardValue(rank);
            ipCombinedValues.add(rankValue);
        }
        ipCombinedValues.sort(Comparator.reverseOrder());

        // Evaluate each player's hand strength
        var oopStrength = evaluateHandStrength(oopCombined);
        var ipStrength = evaluateHandStrength(ipCombined);

        // Compare the hand strengths
        if (oopStrength > ipStrength) {
            return Player.OutOfPosition;
        } else if (ipStrength > oopStrength) {
            return Player.InPosition;
        } else if (oopStrength < 4) {
            // Kicker comparison for trips & pairs
            for (int i = 0; i < 4; i++) {
                if (oopCombinedValues.get(i) > ipCombinedValues.get(i)) {
                    return Player.OutOfPosition;
                } else if (oopCombinedValues.get(i) < ipCombinedValues.get(i)) {
                    return Player.InPosition;
                }
            }
        }

        return Player.Chop;
    }

    // Combines a player's hand and the board into a 7-card hand for evaluation
    private static String[] combineHandsAndBoard(String[] hand, String[] board) {
        String[] combined = new String[7];
        System.arraycopy(hand, 0, combined, 0, hand.length);
        System.arraycopy(board, 0, combined, hand.length, board.length);
        return combined;
    }

    // Evaluates hand strength - a simplified version for pair, three-of-a-kind, and high card
    private static double evaluateHandStrength(String[] cards) {
        // Map to count occurrences of each rank
        var rankCount = new HashMap<Character, Integer>();

        // Count occurrences of each rank (ignore suits for this basic evaluation)
        var suitCounts = new HashMap<>(Map.copyOf(Map.of(
                'h', new int[13],
                'd', new int[13],
                'c', new int[13],
                's', new int[13])));

        for (String card : cards) {
            if (card == null || card.length() == 0) {
                continue;
            }

            var rank = card.charAt(0);
            var suit = card.charAt(1);
            var rankValue = getCardValue(rank);

            var currentCounts = suitCounts.get(suit);
            currentCounts[rankValue] = rankValue + 1;

            suitCounts.put(suit, currentCounts);
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }

        var straightFlushStart = checkStraightFlush(suitCounts);
        var hasStraightFlush = straightFlushStart > 0;
        if (hasStraightFlush) {
            return 8d + straightFlushStart / 20;
        }

        var flushKicker = checkHasFlush(suitCounts);
        var hasFlush = flushKicker > 0;
        var straightStart = checkHasStraight(suitCounts);
        var hasStraight = straightStart > 0;
        var hasPair = false;
        var pairedValue = 0d;
        var pairedValues = new ArrayList<Integer>();
        var hasTwoPair = false;
        var hasThreeOfAKind = false;
        var hasFullHouse = false;
        var hasFourOfAKind = false;
        var fourValue = 0d;
        var tripValue = 0d;
        var highestCard = '2';  // Default to the lowest card

        // Evaluate the hand based on rank counts
        for (Map.Entry<Character, Integer> entry : rankCount.entrySet()) {
            var count = entry.getValue();
            var rank = entry.getKey();
            var rankValue = getCardValue(rank);

            if (count == 2) {
                hasPair = true;
                pairedValue = Math.max(pairedValue, rankValue);
                pairedValues.add(rankValue);
                if (pairedValues.size() >= 2) {
                    hasTwoPair = true;
                }
            } else if (count == 3) {
                hasThreeOfAKind = true;
                tripValue = Math.max(tripValue, rankValue);
                for (var pv : pairedValues) {
                    if (pv != rankValue) {
                        hasFullHouse = true;
                    }
                }
            } else if (count == 4) {
                hasFourOfAKind = true;
                fourValue = rankValue;
            }

            // Track the highest card
            if (rankValue > getCardValue(highestCard)) {
                highestCard = rank;
            }
        }

        // Return hand strength based on evaluation
        if (hasFourOfAKind) {
            return 7d + (fourValue / 20);
        } else if (hasFullHouse) {
            return 6d + tripValue / 20 + pairedValue / 200;
        } else if (hasFlush) {
            return 5d + (flushKicker / 20);
        } else if (hasStraight) {
            return 4d + (straightStart / 20);
        } else if (hasThreeOfAKind) {
            return 3d + (tripValue / 20);  // Three-of-a-kind
        } else if (hasTwoPair) {
            return 2d;
        } else if (hasPair) {
            return 1d + (pairedValue / 20);  // Pair
        } else {
            return getCardValue(highestCard) / 20;  // High card
        }
    }

    // Get the numeric value of a card rank for easier comparison
    private static int getCardValue(char rank) {
        switch (rank) {
            case '2':
                return 0;
            case '3':
                return 1;
            case '4':
                return 2;
            case '5':
                return 3;
            case '6':
                return 4;
            case '7':
                return 5;
            case '8':
                return 6;
            case '9':
                return 7;
            case 'T':
                return 8;
            case 'J':
                return 9;
            case 'Q':
                return 10;
            case 'K':
                return 11;
            case 'A':
                return 12;
            default:
                throw new InputMismatchException("Invalid card: " + rank);  // Error handling for unexpected inputs
        }
    }

    private static double checkStraightFlush(Map<Character, int[]> suitCounts) {
        int maxValue = 0;
        for (var suitRanks : suitCounts.values()) {
            // If there's an ace, start the run at 1 (to account for A-5 flush)
            var run = suitRanks[12] > 0 ? 1 : 0;
            for (var rank : suitRanks) {
                if (rank > 0) {
                    run++;
                } else {
                    run = 0;
                }

                if (run >= 5 && rank > maxValue) {
                    maxValue = rank;
                }
            }
        }

        return maxValue;
    }

    private static double checkHasFlush(Map<Character, int[]> suitCounts) {
        int maxValue = 0;

        for (var suit : suitCounts.keySet()) {
            var count = 0;
            var maxValueForSuit = 0;
            for (var rankValue : suitCounts.get(suit)) {
                maxValueForSuit = Math.max(maxValueForSuit, rankValue);
                if (rankValue > 0) {
                    count++;
                }
            }

            if (count >= 5 && maxValueForSuit > maxValue) {
                maxValue = maxValueForSuit;
            }
        }

        return maxValue;
    }

    private static double checkHasStraight(Map<Character, int[]> suitCounts) {
        var ranks = new int[13];

        for (var suit : suitCounts.keySet()) {
            int i = 0;
            for (var rankValue : suitCounts.get(suit)) {
                if (rankValue > 0) {
                    ranks[i] = rankValue;
                }
                i++;
            }
        }

        // If there's an ace, start the run at 1 (to account for A-5 flush)
        var run = ranks[12] > 0 ? 1 : 0;
        var maxValue = 0;
        for (var rank : ranks) {
            if (rank > 0) {
                run++;
            } else {
                run = 0;
            }

            if (run >= 5) {
                maxValue = rank;
            }
        }

        return maxValue;
    }
}
