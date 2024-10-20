package org.goldenpath.solver.compute;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class EquityCalculatorTest {

    private final HandResolver handResolver = new HandResolver();
    private final EquityCalculator sut = new EquityCalculator(handResolver);

    @Test
    public void testChopOnRiver() {
        var heroHand = new String[]{"Ah", "Ad"};
        var villainHand = new String[]{"As", "Ac"};
        var board = new String[]{"2d", "Jc", "8s", "9d", "7h"};

        var result = sut.handVsHand(heroHand, villainHand, board);

        assertArrayEquals(result, new double[]{0, 0, 1.0});
    }

    @Test
    public void testHeroOnRiver() {
        var heroHand = new String[]{"Ah", "Kh"};
        var villainHand = new String[]{"As", "Qc"};
        var board = new String[]{"Kd", "Jc", "8s", "9d", "7h"};

        var result = sut.handVsHand(heroHand, villainHand, board);

        assertArrayEquals(result, new double[]{1, 0, 0});
    }

    @Test
    public void testPostFlop() {
        var heroHand = new String[]{"Ah", "Kh"};
        var villainHand = new String[]{"7h", "3s"};
        var board = new String[]{"Kd", "Jc", "8s"};

        var result = sut.handVsHand(heroHand, villainHand, board);

        assertArrayEquals(result, new double[]{0.9686868686868687, 0.031313131313131314, 0});
    }

    @Test
    public void testPostTurn() {
        var heroHand = new String[]{"Ah", "Kh"};
        var villainHand = new String[]{"7h", "3s"};
        var board = new String[]{"Kd", "Ac", "8s", "7d"};

        var result = sut.handVsHand(heroHand, villainHand, board);

        assertArrayEquals(result, new double[]{0.9545454545454546, 0.045454545454545456, 0});
    }

    @Test
    public void twoToOne() {
        var heroHand = new String[]{"Ah", "8h"};
        var villainRange = new String[]{"6s", "9s"};
        var board = new String[]{"Ad", "7c", "8c",};

        var result = sut.handVsHand(heroHand, villainRange, board);

        assertArrayEquals(result, new double[]{0.6828282828282828, 0.31717171717171716, 0});
    }

    @Test
    public void straightAndFlushDrawFlip() {
        var heroHand = new String[]{"Ah", "8h"};
        var villainHand = new String[]{"6c", "9c"};
        var board = new String[]{"Ad", "7c", "8c",};

        var result = sut.handVsHand(heroHand, villainHand, board);

        assertArrayEquals(result, new double[]{0.4585858585858586, 0.5414141414141415, 0});
    }

    @Test
    public void testPreflopOneSuitedOneOffsuit() {
        var heroHand = new String[]{"Ah", "Kh"};
        var villainHand = new String[]{"As", "Kc"};
        var board = new String[]{};

        var result = sut.handVsHand(heroHand, villainHand, board);

        assertArrayEquals(result, new double[]{0.07157373924256441, 0.02154348760500472, 0.9068827731524309});
    }

    @Test
    public void testPreflopSuitedChops() {
        var heroHand = new String[]{"Ah", "Kh"};
        var villainHand = new String[]{"As", "Ks"};
        var board = new String[]{};

        var result = sut.handVsHand(heroHand, villainHand, board);

        assertArrayEquals(result, new double[]{0.07157373924256441, 0.07157373924256441, 0.8568525215148712});
    }

    @Test
    public void testHandVsRangeOnRiver() {
        var heroHand = new String[]{"Kh", "Kd"};
        var villainRange = new String[][]{new String[]{"As", "Ac"}, new String[]{"Ks", "Kc"}, new String[]{"Qs", "Qc"}, new String[]{"Js", "Jc"}};
        var board = new String[]{"7h", "3d", "3h", "8c", "2s"};

        var result = sut.handVsRange(heroHand, villainRange, board);

        assertArrayEquals(result, new double[]{0.5, 0.25, 0.25});
    }

    @Test
    public void testHandDrawingDeadVsRangeOnTurn() {
        var heroHand = new String[]{"Kh", "Kd"};
        var villainRange = new String[][]{new String[]{"As", "Ac"}, new String[]{"Ks", "Kc"}, new String[]{"Qs", "Qc"}, new String[]{"Js", "Jc"}};
        var board = new String[]{"7s", "3s", "4s", "8s"};

        var result = sut.handVsRange(heroHand, villainRange, board);

        assertArrayEquals(result, new double[]{0.0, 0.9659090909090909, 0.03409090909090909});
    }

    @Test
    public void testHandVsRangeOnFlop() {
        var heroHand = new String[]{"Kh", "Kd"};
        var villainRange = new String[][]{new String[]{"As", "Ac"}, new String[]{"Ac", "Ad"}, new String[]{"Ks", "Kc"}, new String[]{"Qs", "Qc"}, new String[]{"Js", "Jc"}};
        var board = new String[]{"7h", "3d", "3s", "8c"};

        var result = sut.handVsRange(heroHand, villainRange, board);

        assertArrayEquals(result, new double[]{0.39090909090909093, 0.4090909090909091, 0.2});
    }
}
