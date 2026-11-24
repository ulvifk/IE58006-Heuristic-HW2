package heuristic.solvers;

import heuristic.model.solution.Solution;
import heuristic.solvers.tabusearch.IterationLog;
import java.util.List;
import lombok.Value;

/// What a solver hands back after one run.
@Value
public class SolverResult {

    Solution bestSolution;
    List<IterationLog> iterationLogs; // [iteration - 1] -> the move that iteration selected
}
