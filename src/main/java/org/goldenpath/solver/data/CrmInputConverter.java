package org.goldenpath.solver.data;

import org.goldenpath.solver.components.Board;
import org.goldenpath.solver.components.OtherParams;
import org.goldenpath.solver.components.PositionParams;
import org.goldenpath.solver.compute.model.CrmInput;
import org.goldenpath.solver.compute.RangeProvider;
import org.goldenpath.solver.compute.model.PlayerParams;

public class CrmInputConverter {
    public CrmInput toCrmInput(
            RangeProvider oopRange,
            PositionParams oopParams,
            RangeProvider ipRange,
            PositionParams ipParams,
            OtherParams otherParams,
            Board board
    ) {
        var oop = fromPositionParams(oopParams, oopRange);
        var ip = fromPositionParams(ipParams, ipRange);
        var players = new PlayerParams[]{oop, ip};

        return new CrmInput(
                players,
                Integer.parseInt(otherParams.getValues().raiseLimit()),
                Integer.parseInt(otherParams.getValues().pot()),
                Integer.parseInt(otherParams.getValues().effectiveStack()),
                Double.parseDouble(otherParams.getValues().allinThreshold()),
                board.getBoard()
        );
    }

    private static PlayerParams fromPositionParams(PositionParams positionParams, RangeProvider rangeProvider) {
        return new PlayerParams(
                rangeProvider.range,
                sizesFromText(positionParams.getValues().flop().bet()),
                sizesFromText(positionParams.getValues().flop().raise()),
                sizesFromText(positionParams.getValues().turn().bet()),
                sizesFromText(positionParams.getValues().turn().raise()),
                sizesFromText(positionParams.getValues().river().bet()),
                sizesFromText(positionParams.getValues().river().raise())
        );
    }

    private static double[] sizesFromText(String input) {
        var sizesText = input.strip().split(",");
        var sizes = new double[sizesText.length];
        for (int i = 0; i < sizesText.length; i++) {
            sizes[i] = Double.parseDouble(sizesText[i]);
        }

        return sizes;
    }
}
