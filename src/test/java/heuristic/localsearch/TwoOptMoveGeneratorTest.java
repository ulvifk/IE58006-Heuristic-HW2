package heuristic.localsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import heuristic.model.data.ProblemData;
import heuristic.model.data.ProblemDataParser;
import heuristic.model.solution.Solution;
import heuristic.model.solution.SolutionFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TwoOptMoveGeneratorTest {

    private ProblemData problemData;
    private Solution baselineSolution;

    @BeforeEach
    void loadBaseline() throws IOException {
        problemData = ProblemDataParser.fromJson(Files.readString(Path.of("instances/tsp20.json")));
        baselineSolution = SolutionFactory.build(problemData.getInitialTour(), problemData.getDistanceMatrix());
    }

    @Test
    void forEachMoveHandsOverEveryPairOrderedByIThenJ() {
        var positions = new ArrayList<List<Integer>>();

        new TwoOptMoveGenerator(problemData.getDistanceMatrix()).forEachMove(baselineSolution, move -> {
            positions.add(getPositions(move));
            return true;
        });

        // sum over i = 0..17 of (18 - i) pairs with j >= i + 2
        assertEquals(171, positions.size());
        assertEquals(List.of(0, 2), positions.getFirst());
        assertEquals(List.of(0, 19), positions.get(17));
        assertEquals(List.of(1, 3), positions.get(18));
        assertEquals(List.of(17, 19), positions.getLast());
    }

    @Test
    void forEachMoveStopsWhenTheConsumerSaysSo() {
        var positions = new ArrayList<List<Integer>>();

        new TwoOptMoveGenerator(problemData.getDistanceMatrix()).forEachMove(baselineSolution, move -> {
            positions.add(getPositions(move));
            return positions.size() < 3;
        });

        assertEquals(List.of(List.of(0, 2), List.of(0, 3), List.of(0, 4)), positions);
    }

    private static List<Integer> getPositions(TwoOptMove move) {
        return List.of(move.getI(), move.getJ());
    }
}
