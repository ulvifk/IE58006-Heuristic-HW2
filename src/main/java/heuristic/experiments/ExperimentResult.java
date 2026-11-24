package heuristic.experiments;

import heuristic.model.data.ProblemData;
import heuristic.solvers.SolverResult;
import heuristic.solvers.tabusearch.TabuSearchConfig;
import lombok.Value;

/// One run as written to disk: the instance it solved, the configuration it ran with and what the solver returned.
@Value
public class ExperimentResult {

    ProblemData problemData;
    TabuSearchConfig config;
    SolverResult solverResult;
}
