package heuristic.solvers.tabusearch;

import heuristic.model.data.Edge;
import heuristic.model.solution.Solution;
import java.util.List;
import lombok.Value;

/// The move one iteration selected and the solutions after it.
@Value
public class IterationLog {

    int iteration; // 1 for the first selected move

    int i; // the 2-opt move's positions
    int j;
    List<Edge> removedEdges;
    List<Edge> insertedEdges;
    double delta; // tour length change; positive for a worsening move

    boolean tabu; // an inserted edge was tabu
    boolean aspiring; // its length beats the best-so-far, so aspiration would admit it even if tabu

    Solution current; // after the move
    Solution best;
}
