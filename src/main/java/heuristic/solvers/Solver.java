package heuristic.solvers;

/// A search over closed tours of one problem instance, configured before solve is called.
public interface Solver {

    /// Runs the search once and returns the best tour it found.
    SolverResult solve();
}
