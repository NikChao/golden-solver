package org.goldenpath.solver.compute;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RangeProviderTest {
    private final RangeProvider sut = new RangeProvider();

    @Test
    public void testRangeProviderWithUnsuited() {
        var range = "AA,KK,QQ,JJ,TT,99,88,77,66,55,44,33,22,AKs,AQs,AJs,ATs,A9s,A8s,A7s,A6s";
        var hand = "A5";

        var result = sut.findMatchingHandInRange(hand, range);

        assertTrue(result.isEmpty());
    }
}
