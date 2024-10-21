package org.goldenpath.solver.compute;

import org.goldenpath.solver.components.Board;
import org.goldenpath.solver.components.OtherParams;
import org.goldenpath.solver.components.PositionParams;
import org.goldenpath.solver.compute.model.GameTree;
import org.goldenpath.solver.data.CrmInputConverter;
import org.goldenpath.solver.data.RangeConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CrmSolverTest {
    private final HandResolver handResolver = new HandResolver();
    private final RangeConverter rangeConverter = new RangeConverter();
    private final CrmInputConverter converter = new CrmInputConverter(rangeConverter);

    @Mock
    private PositionParams oopParams;

    @Mock
    private PositionParams ipParams;

    @Mock
    private OtherParams otherParams;

    @Mock
    private Board board;

    @Test
    public void testItRuns() {
        var rangeProvider = new RangeProvider(30);

        var oopValues = new PositionParams.Values(
                new PositionParams.Spot("0.5", "0.5"),
                new PositionParams.Spot("0.5", "0.5"),
                new PositionParams.Spot("0.5", "0.5")
        );
        var ipValues = oopValues;
        when(oopParams.getValues()).thenReturn(oopValues);
        when(ipParams.getValues()).thenReturn(ipValues);

        var otherParamValues = new OtherParams.Values("1", "50", "200", "0.67");
        when(otherParams.getValues()).thenReturn(otherParamValues);
        when(board.getBoard()).thenReturn("Qh,5d,8s");

        var input = converter.toCrmInput(rangeProvider, oopParams, rangeProvider, ipParams, otherParams, board);
        var gameTree = new GameTree(input);

        var sut = new CrmSolver(handResolver, input, gameTree);


        var result = sut.solve(5);

        assertNotNull(result);
    }
}
