package heuristic.localsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import heuristic.model.data.ProblemDataParser;
import heuristic.model.data.Tour;
import heuristic.model.solution.Solution;
import heuristic.model.solution.SolutionFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class BestImprovementNeighborhoodTest {

    private static final Solution START = new Solution(new Tour(List.of()), 0.0);

    @Test
    void improveReturnsTheSameSolutionWhenNothingImproves() {
        var generator = new FakeMoveGenerator(List.of(new FakeMove(true, 0.0), new FakeMove(false, 9.0)));

        assertSame(START, new BestImprovementNeighborhood<>(generator).improve(START));
    }

    @Test
    void improveAppliesTheHighestGainMove() {
        var best = new FakeMove(true, 7.0);
        var generator = new FakeMoveGenerator(List.of(new FakeMove(true, 1.0), best, new FakeMove(true, 3.0)));

        assertSame(best.getResult(), new BestImprovementNeighborhood<>(generator).improve(START));
    }

    @Test
    void improveScansEveryMove() {
        var generator = new FakeMoveGenerator(List.of(new FakeMove(true, 7.0), new FakeMove(true, 1.0)));

        new BestImprovementNeighborhood<>(generator).improve(START);

        assertEquals(2, generator.getHandedOverCount());
    }

    // part A1 calls this tour a 2-opt local optimum
    @Test
    void improveFindsNoImprovingTwoOptMoveOnThePartA1Tour() throws IOException {
        var problemData = ProblemDataParser.fromJson(Files.readString(Path.of("instances/tsp20.json")));
        var tour = new Tour(List.of(0, 7, 6, 15, 16, 17, 14, 13, 18, 19, 12, 11, 10, 3, 2, 9, 4, 5, 8, 1));
        var start = SolutionFactory.build(tour, problemData.getDistanceMatrix());
        var neighborhood = new BestImprovementNeighborhood<>(new TwoOptMoveGenerator(problemData.getDistanceMatrix()));

        assertSame(start, neighborhood.improve(start));
    }
}
