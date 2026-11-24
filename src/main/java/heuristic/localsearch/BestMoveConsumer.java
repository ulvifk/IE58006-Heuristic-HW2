package heuristic.localsearch;

import static heuristic.Constants.ZERO_TOLERANCE;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import lombok.RequiredArgsConstructor;

/// Keeps the feasible, admissible move with the lowest score over a whole scan, even when that move worsens the
/// solution. The caller decides which moves are admissible and how they are scored.
///
/// - a later move replaces the best only when its score is lower by more than the tolerance, so among equal scores
///   the first in the generator's order wins
@RequiredArgsConstructor
public class BestMoveConsumer<M extends Move> implements MoveConsumer<M> {

    private final Predicate<M> isAdmissible;
    private final ToDoubleFunction<M> getScore;

    private M bestMove; // null until a feasible, admissible move arrives
    private double bestScore = Double.POSITIVE_INFINITY;

    @Override
    public boolean accept(M move) {
        if (!move.isFeasible()) return true;
        if (!isAdmissible.test(move)) return true;

        var score = getScore.applyAsDouble(move);
        if (score >= bestScore - ZERO_TOLERANCE) return true;

        bestMove = move;
        bestScore = score;

        return true;
    }

    /// Empty when no feasible move was admissible.
    public Optional<M> getBestMove() {
        return Optional.ofNullable(bestMove);
    }
}
