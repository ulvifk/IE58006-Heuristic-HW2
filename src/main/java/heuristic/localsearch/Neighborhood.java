package heuristic.localsearch;

import heuristic.model.solution.Solution;

/// One improvement step over a neighbourhood.
public interface Neighborhood {

    /// Returns the improved solution, or the very same solution object when no move improves it, so the caller
    /// detects "no improvement" by identity.
    Solution improve(Solution solution);
}
