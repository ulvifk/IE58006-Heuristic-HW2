package heuristic.localsearch;

import static heuristic.Constants.ZERO_TOLERANCE;

import heuristic.model.solution.Solution;
import lombok.RequiredArgsConstructor;

/// Applies the move with the highest gain, among moves that improve by more than the tolerance.
@RequiredArgsConstructor
public class BestImprovementNeighborhood<M extends Move> implements Neighborhood {

    private final MoveGenerator<M> moveGenerator;

    @Override
    public Solution improve(Solution solution) {
        var consumer = new BestMoveConsumer<M>(move -> move.getGain() > ZERO_TOLERANCE, move -> -move.getGain());
        moveGenerator.forEachMove(solution, consumer);

        var bestMove = consumer.getBestMove();
        if (bestMove.isEmpty()) return solution;

        return bestMove.get().apply();
    }
}
