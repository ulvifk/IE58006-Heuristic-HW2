package heuristic.solvers.tabusearch;

import heuristic.localsearch.TwoOptMoveGenerator;
import heuristic.model.data.ProblemData;
import heuristic.model.solution.SolutionFactory;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TabuSearchSolverFactory {

    /// Builds a run from the initial tour with empty tabu and frequency memories.
    public static TabuSearchSolver build(TabuSearchConfig config, ProblemData problemData) {
        var cityCount = problemData.getCities().size();
        var initialSolution = SolutionFactory.build(problemData.getInitialTour(), problemData.getDistanceMatrix());

        return new TabuSearchSolver(
                config,
                new TwoOptMoveGenerator(problemData.getDistanceMatrix()),
                new Random(config.getSeed()),
                new TabuList(cityCount),
                new EdgeInsertionCounts(cityCount),
                initialSolution);
    }
}
