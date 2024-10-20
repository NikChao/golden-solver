package org.goldenpath.solver.compute;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandResolverTest {
    private final HandResolver sut = new HandResolver();

    @Test
    public void testStraightBeatsPair() {
        var inPositionHand = new String[]{"Qh", "7h"};
        var outOfPositionHand = new String[]{"6c", "7s"};
        var board = new String[]{"Qs", "5h", "8d", "9c", "2d"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.OutOfPosition);
    }

    @Test
    public void testFlushBeatsStraight() {
        var inPositionHand = new String[]{"Qh", "7h"};
        var outOfPositionHand = new String[]{"6c", "7s"};
        var board = new String[]{"Qs", "5h", "8h", "9h", "2d"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);
    }

    @Test
    public void testStraightBeatsStraight() {
        var inPositionHand = new String[]{"Qh", "Jh"};
        var outOfPositionHand = new String[]{"6c", "7s"};
        var board = new String[]{"Qs", "5c", "8c", "9c", "Td"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);
    }

    @Test
    public void testPairBeatsPair() {
        var inPositionHand = new String[]{"Qh", "2h"};
        var outOfPositionHand = new String[]{"6c", "Ks"};
        var board = new String[]{"Qs", "Kc", "8c", "9c", "Td"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.OutOfPosition);
    }

    @Test
    public void flushBeatsFlush() {
        var inPositionHand = new String[]{"Qh", "2h"};
        var outOfPositionHand = new String[]{"Kh", "3h"};
        var board = new String[]{"Qs", "Kc", "8h", "9h", "Th"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.OutOfPosition);
    }

    @Test
    public void fullHouseBeatsFlush() {
        var inPositionHand = new String[]{"Qh", "2h"};
        var outOfPositionHand = new String[]{"Kh", "7d"};
        var board = new String[]{"7h", "Kc", "Ks", "9h", "Th"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.OutOfPosition);
    }

    @Test
    public void fourOfAKindBeatsFlush() {
        var inPositionHand = new String[]{"Qh", "2h"};
        var outOfPositionHand = new String[]{"Kh", "Kd"};
        var board = new String[]{"7h", "Kc", "Ks", "9h", "Th"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.OutOfPosition);
    }

    @Test
    public void fourOfAKindBeatsFullHouse() {
        var inPositionHand = new String[]{"Qh", "2h"};
        var outOfPositionHand = new String[]{"Kh", "Kd"};
        var board = new String[]{"Qd", "Kc", "Ks", "Qc", "Th"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.OutOfPosition);
    }

    @Test
    public void chop() {
        var inPositionHand = new String[]{"Qh", "2h"};
        var outOfPositionHand = new String[]{"Qs", "2d"};
        var board = new String[]{"Qd", "Kc", "Ks", "Qc", "Th"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.Chop);
    }

    @Test
    public void checkKickerOnPair() {
        var inPositionHand = new String[]{"Qh", "Th"};
        var outOfPositionHand = new String[]{"Qs", "9d"};
        var board = new String[]{"Qd", "2d", "3d", "4c", "8s"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);

    }

    @Test
    public void twoPairBeatsPair() {
        var inPositionHand = new String[]{"Qh", "Th"};
        var outOfPositionHand = new String[]{"Qs", "9d"};
        var board = new String[]{"Qd", "9d", "3d", "4c", "8s"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.OutOfPosition);

    }

    @Test
    public void setBeatsTwoPair() {
        var inPositionHand = new String[]{"Qh", "Th"};
        var outOfPositionHand = new String[]{"Qs", "9d"};
        var board = new String[]{"Qd", "9d", "Td", "Tc", "8s"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);
    }

    @Test
    public void twoPairOverTwoPairBottom() {
        var inPositionHand = new String[]{"Qh", "Th"};
        var outOfPositionHand = new String[]{"Qs", "9d"};
        var board = new String[]{"Qd", "9d", "Td", "3c", "4s"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);
    }

    @Test
    public void twoPairOverTwoPairTop() {
        var inPositionHand = new String[]{"Qs", "Td"};
        var outOfPositionHand = new String[]{"Kh", "Ts"};
        var board = new String[]{"Qd", "Kd", "Tc", "3c", "4s"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.OutOfPosition);
    }

    @Test
    public void twoPairChop() {
        var inPositionHand = new String[]{"Qh", "Th"};
        var outOfPositionHand = new String[]{"Qs", "Tc"};
        var board = new String[]{"Qd", "9d", "Td", "3c", "4s"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.Chop);
    }

    @Test
    public void straightFlushBeatsStraightFlush() {
        var inPositionHand = new String[]{"Ah", "Kh"};
        var outOfPositionHand = new String[]{"7h", "6h"};
        var board = new String[]{"Qh", "Jh", "Th", "9h", "8h"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);
    }

    @Test
    public void straightFlushBeatsFourOfAKind() {
        var inPositionHand = new String[]{"Ah", "Kh"};
        var outOfPositionHand = new String[]{"Qs", "Ts"};
        var board = new String[]{"Qh", "Jh", "Th", "Td", "Tc"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);
    }

    @Test
    public void straightFlushChop() {
        var inPositionHand = new String[]{"2d", "3d"};
        var outOfPositionHand = new String[]{"2c", "3c"};
        var board = new String[]{"As", "Ks", "Qs", "Js", "Ts"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.Chop);
    }

    @Test
    public void aceToFiveStraight() {
        var inPositionHand = new String[]{"Ad", "5h"};
        var outOfPositionHand = new String[]{"2c", "3c"};
        var board = new String[]{"2s", "3d", "4c", "Jh", "Th"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);
    }

    @Test
    public void aceToFiveStraightFlush() {
        var inPositionHand = new String[]{"Ad", "5d"};
        var outOfPositionHand = new String[]{"2c", "4h"};
        var board = new String[]{"2d", "3d", "4d", "4c", "4s"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);
    }

    @Test
    public void pocketKingsFlush() {
        var inPositionHand = new String[]{"Kh", "Kd"};
        var outOfPositionHand = new String[]{"Kc", "Ks"};
        var board = new String[]{"7s", "3s", "4s", "8s", "Ah"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.OutOfPosition);
    }

    @Test
    public void pocketKingsVsAcesBoardStraightFlush() {
        var inPositionHand = new String[]{"Kh", "Kd"};
        var outOfPositionHand = new String[]{"Ah", "Ad"};
        var board = new String[]{"3h", "4h", "5h", "6h", "7h"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.Chop);
    }

    @Test
    public void testBoatOverBoat() {
        var inPositionHand = new String[]{"As", "Ts"};
        var outOfPositionHand = new String[]{"Qh", "Qd"};
        var board = new String[]{"Kh", "Kc", "Kd", "Th", "Ad"};

        var result = sut.compareHands(outOfPositionHand, inPositionHand, board);

        assertEquals(result, HandResolver.Player.InPosition);
    }
}
