package org.goldenpath.solver.data;

import org.goldenpath.solver.components.Board;
import org.goldenpath.solver.components.OtherParams;
import org.goldenpath.solver.components.PositionParams;
import org.goldenpath.solver.compute.RangeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


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

    private final RangeConverter rangeConverter = new RangeConverter();
    private final CrmInputConverter sut = new CrmInputConverter(rangeConverter);

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
        Mockito.when(board.getBoard()).thenReturn("Qh,Js,8c");
    }

    @Test
    public void testConvert() {
        RangeProvider oopRange = new RangeProvider(10);
        RangeProvider ipRange = new RangeProvider(30);

        var result = sut.toCrmInput(oopRange, oopParams, ipRange, ipParams, otherParams, board);

        assertEquals(String.join(",", result.players()[0].range()), "AhAd,AhAc,AhAs,AdAc,AdAs,AcAs,KhKd,KhKc,KhKs,KdKc,KdKs,KcKs," +
                "QhQd,QhQc,QhQs,QdQc,QdQs,QcQs,AhKh,AdKd,AcKc,AsKs,JhJd,JhJc,JhJs,JdJc,JdJs,JcJs,AhQh,AdQd,AcQc," +
                "AsQs,KhQh,KdQd,KcQc,KsQs,AhJh,AdJd,AcJc,AsJs,KhJh,KdJd,KcJc,KsJs,ThTd,ThTc,ThTs,TdTc,TdTs," +
                "TcTs,AhKh,AhKd,AhKc,AhKs,AdKh,AdKd,AdKc,AdKs,AcKh,AcKd,AcKc,AcKs,AsKh,AsKd,AsKc,AsKs,AhTh," +
                "AdTd,AcTc,AsTs,QhJh,QdJd,QcJc,QsJs,KhTh,KdTd,KcTc,KsTs,QhTh,QdTd,QcTc,QsTs,JhTh,JdTd,JcTc,JsTs,9h9d,9h9c," +
                "9h9s,9d9c,9d9s,9c9s");
        assertEquals(result.players()[0].flopBetSizes()[0], 0.25);
        assertEquals(result.players()[0].flopBetSizes()[1], 0.5);
        assertEquals(result.players()[0].flopRaiseSizes()[0], 0.25);
        assertEquals(result.players()[0].flopRaiseSizes()[1], 0.75);
    }
}
