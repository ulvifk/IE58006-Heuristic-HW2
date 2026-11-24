package heuristic.localsearch;

/// Receives the moves a generator builds, one at a time, and decides whether the scan goes on.
public interface MoveConsumer<M extends Move> {

    /// Returns whether the generator should keep scanning.
    boolean accept(M move);
}
