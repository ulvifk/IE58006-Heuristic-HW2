package heuristic.localsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import heuristic.model.data.ProblemData;
import heuristic.model.data.ProblemDataParser;
import heuristic.model.data.Tour;
import heuristic.model.solution.Solution;
import heuristic.model.solution.SolutionFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TwoOptMoveTest {

    private static final double TOLERANCE = 1e-9;

    private ProblemData problemData;
    private Solution baselineSolution;

    @BeforeEach
    void loadBaseline() throws IOException {
        problemData = ProblemDataParser.fromJson(Files.readString(Path.of("instances/tsp20.json")));
        baselineSolution = new Solution(problemData.getInitialTour(), 94.15);
    }

    // part A1: removes (10, 3) and (2, 9), adds (10, 2) and (3, 9); delta = 2.61 + 1.79 - 1.91 - 2.33
    @Test
    void getGainIsTheNegativeDeltaOfTheAssignmentMove() {
        var tour = new Tour(List.of(0, 7, 6, 15, 16, 17, 14, 13, 18, 19, 12, 11, 10, 3, 2, 9, 4, 5, 8, 1));
        var localOptimum = SolutionFactory.build(tour, problemData.getDistanceMatrix());
        var move = new TwoOptMove(localOptimum, problemData.getDistanceMatrix(), 12, 14);

        assertEquals(41.15, localOptimum.getCost(), TOLERANCE);
        assertEquals(-0.16, move.getGain(), TOLERANCE);
        assertEquals(41.31, move.apply().getCost(), TOLERANCE);
    }

    @Test
    void applyReversesTheSubpathAndUpdatesTheCost() {
        var move = new TwoOptMove(baselineSolution, problemData.getDistanceMatrix(), 1, 9);

        var solution = move.apply();

        assertEquals(
                List.of(0, 4, 5, 8, 6, 2, 9, 3, 7, 1, 15, 12, 18, 10, 16, 13, 11, 17, 14, 19),
                solution.getTour().getCityIds());
        assertEquals(95.30, solution.getCost(), TOLERANCE);
        assertEquals(
                SolutionFactory.build(solution.getTour(), problemData.getDistanceMatrix())
                        .getCost(),
                solution.getCost(),
                TOLERANCE);
    }

    @Test
    void applyWrapsTheSecondEdgeToTheFirstCityAtTheLastPosition() {
        var move = new TwoOptMove(baselineSolution, problemData.getDistanceMatrix(), 3, 19);

        var solution = move.apply();

        assertEquals(
                SolutionFactory.build(solution.getTour(), problemData.getDistanceMatrix())
                        .getCost(),
                solution.getCost(),
                TOLERANCE);
    }

    @Test
    void isFeasibleOnlyForMovesThatChangeTheTour() {
        var distanceMatrix = problemData.getDistanceMatrix();

        assertTrue(new TwoOptMove(baselineSolution, distanceMatrix, 1, 9).isFeasible());
        assertTrue(new TwoOptMove(baselineSolution, distanceMatrix, 0, 18).isFeasible());
        assertFalse(new TwoOptMove(baselineSolution, distanceMatrix, 4, 5).isFeasible());
        assertFalse(new TwoOptMove(baselineSolution, distanceMatrix, 0, 19).isFeasible());
    }
}
