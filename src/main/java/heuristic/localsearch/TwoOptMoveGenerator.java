package heuristic.localsearch;

import heuristic.model.data.DistanceMatrix;
import heuristic.model.solution.Solution;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TwoOptMoveGenerator implements MoveGenerator<TwoOptMove> {

    private final DistanceMatrix distanceMatrix;

    /// Every 2-opt pair with j >= i + 2, ordered by i and then j; e.g. 20 cities -> 171 moves, from (0, 2) to
    /// (17, 19), of which (0, 19) is the infeasible full-tour reversal.
    @Override
    public void forEachMove(Solution solution, MoveConsumer<TwoOptMove> consumer) {
        var cityCount = solution.getTour().getCityIds().size();

        for (int i = 0; i < cityCount - 2; i++) {
            for (int j = i + 2; j < cityCount; j++) {
                var move = new TwoOptMove(solution, distanceMatrix, i, j);
                if (!consumer.accept(move)) return;
            }
        }
    }
}
