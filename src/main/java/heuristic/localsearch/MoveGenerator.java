package heuristic.localsearch;

import heuristic.model.solution.Solution;

/// Builds the neighbourhood of a solution one move at a time, never as a full list.
public interface MoveGenerator<M extends Move> {

    /// Hands each move to the consumer in a fixed order and returns as soon as accept answers false, so it never
    /// builds more moves than the consumer asked for. The fixed order makes ties between equally good moves
    /// deterministic.
    void forEachMove(Solution solution, MoveConsumer<M> consumer);
}
