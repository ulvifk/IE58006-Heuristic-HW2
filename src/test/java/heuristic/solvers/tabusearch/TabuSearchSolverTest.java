package heuristic.solvers.tabusearch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import heuristic.localsearch.BestMoveConsumer;
import heuristic.localsearch.TwoOptMove;
import heuristic.localsearch.TwoOptMoveGenerator;
import heuristic.model.data.Edge;
import heuristic.model.data.ProblemData;
import heuristic.model.data.ProblemDataParser;
import heuristic.model.data.Tour;
import heuristic.model.solution.SolutionFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TabuSearchSolverTest {

    private static final double BASELINE_COST = 94.15;
    private static final double TOLERANCE = 1e-9;

    private ProblemData problemData;

    @BeforeEach
    void loadInstance() throws IOException {
        problemData = ProblemDataParser.fromJson(Files.readString(Path.of("instances/tsp20.json")));
    }

    @Test
    void baselineRunLogsEveryIterationWithConsistentLengths() {
        var result = TabuSearchSolverFactory.build(buildFixedTenureConfig(7), problemData)
                .solve();

        var iterationLogs = result.getIterationLogs();
        assertEquals(100, iterationLogs.size());
        assertEquals(1, iterationLogs.getFirst().getIteration());
        assertEquals(result.getBestSolution(), iterationLogs.getLast().getBest());

        var previousCurrentCost = BASELINE_COST;
        var previousBestCost = BASELINE_COST;
        for (var iterationLog : iterationLogs) {
            var current = iterationLog.getCurrent();
            var bestCost = iterationLog.getBest().getCost();

            assertEquals(previousCurrentCost + iterationLog.getDelta(), current.getCost(), TOLERANCE);
            assertEquals(getTrueCost(current.getTour()), current.getCost(), TOLERANCE);
            assertEquals(Math.min(previousBestCost, current.getCost()), bestCost, TOLERANCE);

            previousCurrentCost = current.getCost();
            previousBestCost = bestCost;
        }
        assertTrue(result.getBestSolution().getCost() < BASELINE_COST);
    }

    @Test
    void firstIterationSelectsTheLowestDeltaMoveOfTheBaseline() {
        var initialSolution = SolutionFactory.build(problemData.getInitialTour(), problemData.getDistanceMatrix());
        var consumer = new BestMoveConsumer<TwoOptMove>(move -> true, move -> -move.getGain());
        new TwoOptMoveGenerator(problemData.getDistanceMatrix()).forEachMove(initialSolution, consumer);
        var expected = consumer.getBestMove().orElseThrow();

        var firstLog = TabuSearchSolverFactory.build(buildFixedTenureConfig(7), problemData)
                .solve()
                .getIterationLogs()
                .getFirst();

        assertEquals(List.of(expected.getI(), expected.getJ()), List.of(firstLog.getI(), firstLog.getJ()));
    }

    @Test
    void loggedEdgesAreTheOnesTheMoveRemovedAndInserted() {
        var iterationLogs = TabuSearchSolverFactory.build(buildFixedTenureConfig(7), problemData)
                .solve()
                .getIterationLogs();

        var previousTour = problemData.getInitialTour();
        for (var iterationLog : iterationLogs) {
            var currentTour = iterationLog.getCurrent().getTour();
            for (var edge : iterationLog.getRemovedEdges()) {
                assertTrue(getEdges(previousTour).contains(getUndirected(edge)));
                assertFalse(getEdges(currentTour).contains(getUndirected(edge)));
            }
            for (var edge : iterationLog.getInsertedEdges()) {
                assertTrue(getEdges(currentTour).contains(getUndirected(edge)));
            }

            previousTour = currentTour;
        }
    }

    // replays the tabu list from the log: a move is aspiring when it beats the best-so-far, and a tabu move is only
    // selected when it is aspiring
    @Test
    void selectsATabuMoveOnlyWhenItIsAspiring() {
        var iterationLogs = TabuSearchSolverFactory.build(buildFixedTenureConfig(7), problemData)
                .solve()
                .getIterationLogs();

        var tabuList = new TabuList(20);
        var previousBestCost = BASELINE_COST;
        for (var iterationLog : iterationLogs) {
            var iteration = iterationLog.getIteration();
            var tabu = iterationLog.getInsertedEdges().stream().anyMatch(edge -> tabuList.isTabu(edge, iteration));

            assertEquals(tabu, iterationLog.isTabu());
            assertEquals(iterationLog.getCurrent().getCost() < previousBestCost - 1e-6, iterationLog.isAspiring());
            if (tabu) {
                assertTrue(iterationLog.isAspiring());
            }

            iterationLog.getRemovedEdges().forEach(edge -> tabuList.forbid(edge, iteration, 7));
            previousBestCost = iterationLog.getBest().getCost();
        }
    }

    @Test
    void tenureZeroNeverMarksAMoveTabu() {
        var iterationLogs = TabuSearchSolverFactory.build(buildFixedTenureConfig(0), problemData)
                .solve()
                .getIterationLogs();

        assertTrue(iterationLogs.stream().noneMatch(IterationLog::isTabu));
    }

    @Test
    void frequencyPenaltyRanksMovesButLengthsStayTrue() {
        var result = TabuSearchSolverFactory.build(new TabuSearchConfig(100, 0, 0, 0.40, 0), problemData)
                .solve();

        for (var iterationLog : result.getIterationLogs()) {
            var current = iterationLog.getCurrent();
            assertEquals(getTrueCost(current.getTour()), current.getCost(), TOLERANCE);
        }
    }

    @Test
    void sameSeedGivesTheSameRandomTenureRun() {
        var config = new TabuSearchConfig(100, 5, 10, 0.0, 3);

        var first = TabuSearchSolverFactory.build(config, problemData).solve();
        var second = TabuSearchSolverFactory.build(config, problemData).solve();

        assertEquals(first.getIterationLogs(), second.getIterationLogs());
    }

    // no Part C configuration runs out of admissible moves before lmax
    @Test
    void everyPartCConfigurationCompletesAllIterations() {
        for (var tenure : List.of(0, 1, 3, 5, 7, 10, 15, 20)) {
            assertCompletes(buildFixedTenureConfig(tenure));
        }
        for (var range : List.of(List.of(2, 5), List.of(5, 10), List.of(7, 14), List.of(10, 18))) {
            for (int seed = 0; seed < 10; seed++) {
                assertCompletes(new TabuSearchConfig(100, range.getFirst(), range.getLast(), 0.0, seed));
            }
        }
        for (var weight : List.of(0.0, 0.05, 0.10, 0.20, 0.40)) {
            assertCompletes(new TabuSearchConfig(100, 0, 0, weight, 0));
        }
    }

    private void assertCompletes(TabuSearchConfig config) {
        var result = assertDoesNotThrow(
                () -> TabuSearchSolverFactory.build(config, problemData).solve());

        assertEquals(100, result.getIterationLogs().size());
    }

    private double getTrueCost(Tour tour) {
        return SolutionFactory.build(tour, problemData.getDistanceMatrix()).getCost();
    }

    private static TabuSearchConfig buildFixedTenureConfig(int tenure) {
        return new TabuSearchConfig(100, tenure, tenure, 0.0, 0);
    }

    private static Set<List<Integer>> getEdges(Tour tour) {
        var cityIds = tour.getCityIds();
        var edges = new HashSet<List<Integer>>();
        for (int position = 0; position < cityIds.size(); position++) {
            edges.add(getUndirected(new Edge(cityIds.get(position), cityIds.get((position + 1) % cityIds.size()))));
        }

        return edges;
    }

    private static List<Integer> getUndirected(Edge edge) {
        var first = Math.min(edge.getFirstCityId(), edge.getSecondCityId());
        var second = Math.max(edge.getFirstCityId(), edge.getSecondCityId());
        return List.of(first, second);
    }
}
