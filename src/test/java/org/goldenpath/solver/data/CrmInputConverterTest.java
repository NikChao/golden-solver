package org.goldenpath.solver.data;

import org.goldenpath.solver.components.Board;
import org.goldenpath.solver.components.OtherParams;
import org.goldenpath.solver.components.PositionParams;
import org.goldenpath.solver.compute.RangeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.Position;

@ExtendWith(MockitoExtension.class)
public class CrmInputConverterTest {
    @Mock
    private PositionParams oopParams;

    @Mock
    private PositionParams ipParams;

    @Mock
    private OtherParams otherParams;

    @Mock
    private Board board;

    private final CrmInputConverter sut = new CrmInputConverter();

    @BeforeEach
    public void setup() {
        var someValues = new PositionParams.Values(
                new PositionParams.Spot("0.25, 0.5", "0.25, 0.75"),
                new PositionParams.Spot("0.25, 0.5", "0.25, 0.75"),
                new PositionParams.Spot("0.25, 0.5", "0.25, 0.75")
        );
        Mockito.when(oopParams.getValues()).thenReturn(someValues);
        Mockito.when(ipParams.getValues()).thenReturn(someValues);

        var otherParamValues = new OtherParams.Values(
                "3",
                "50",
                "200",
                "0.67"
        );
        Mockito.when(otherParams.getValues()).thenReturn(otherParamValues);
    }

    @Test
    public void testConvert() {
        RangeProvider oopRange = new RangeProvider(10);
        RangeProvider ipRange = new RangeProvider(30);

        var result = sut.toCrmInput(oopRange, oopParams, ipRange, ipParams, otherParams, board);

        assertEquals(result.players()[0].range(), "AA,KK,QQ,AKs,JJ,AQs,KQs,AJs,KJs,TT,AK,ATs,QJs,KTs,QTs,JTs,99");
        assertEquals(result.players()[0].flopBetSizes()[0], 0.25);
        assertEquals(result.players()[0].flopBetSizes()[1], 0.5);
        assertEquals(result.players()[0].flopRaiseSizes()[0], 0.25);
        assertEquals(result.players()[0].flopRaiseSizes()[1], 0.75);
    }
}
