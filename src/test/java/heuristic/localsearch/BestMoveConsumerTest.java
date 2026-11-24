package heuristic.localsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import heuristic.model.data.ProblemDataParser;
import heuristic.model.solution.SolutionFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class BestMoveConsumerTest {

    @Test
    void keepsTheMoveWithTheLowestScoreEvenWhenItWorsens() {
        var best = new FakeMove(true, -1.0);
        var consumer = new BestMoveConsumer<FakeMove>(move -> true, move -> -move.getGain());

        consumer.accept(new FakeMove(true, -3.0));
        consumer.accept(best);
        consumer.accept(new FakeMove(true, -2.0));

        assertSame(best, consumer.getBestMove().orElseThrow());
    }

    @Test
    void skipsInfeasibleAndInadmissibleMoves() {
        var admissible = new FakeMove(true, 1.0);
        var inadmissible = new FakeMove(true, 9.0);
        var consumer = new BestMoveConsumer<FakeMove>(move -> move != inadmissible, move -> -move.getGain());

        consumer.accept(new FakeMove(false, 8.0));
        consumer.accept(inadmissible);
        consumer.accept(admissible);

        assertSame(admissible, consumer.getBestMove().orElseThrow());
    }

    @Test
    void keepsTheFirstOfEquallyScoredMoves() {
        var first = new FakeMove(true, 2.0);
        var consumer = new BestMoveConsumer<FakeMove>(move -> true, move -> -move.getGain());

        consumer.accept(first);
        consumer.accept(new FakeMove(true, 2.0));

        assertSame(first, consumer.getBestMove().orElseThrow());
    }

    @Test
    void isEmptyWhenNoMoveIsAdmissible() {
        var consumer = new BestMoveConsumer<FakeMove>(move -> false, move -> -move.getGain());

        consumer.accept(new FakeMove(true, 1.0));

        assertTrue(consumer.getBestMove().isEmpty());
    }

    @Test
    void alwaysAsksForMoreMoves() {
        var consumer = new BestMoveConsumer<FakeMove>(move -> true, move -> -move.getGain());

        assertTrue(consumer.accept(new FakeMove(false, 1.0)));
        assertTrue(consumer.accept(new FakeMove(true, 1.0)));
        assertTrue(consumer.accept(new FakeMove(true, 0.0)));
    }

    @Test
    void findsTheBestTwoOptMoveOfTheBaselineByItsLowestDelta() throws IOException {
        var problemData = ProblemDataParser.fromJson(Files.readString(Path.of("instances/tsp20.json")));
        var solution = SolutionFactory.build(problemData.getInitialTour(), problemData.getDistanceMatrix());
        var generator = new TwoOptMoveGenerator(problemData.getDistanceMatrix());

        var consumer = new BestMoveConsumer<TwoOptMove>(move -> true, move -> -move.getGain());
        generator.forEachMove(solution, consumer);
        var bestMove = consumer.getBestMove().orElseThrow();

        var highestGain = new double[] {Double.NEGATIVE_INFINITY};
        generator.forEachMove(solution, move -> {
            if (move.isFeasible()) highestGain[0] = Math.max(highestGain[0], move.getGain());
            return true;
        });
        assertEquals(highestGain[0], bestMove.getGain());
    }
}
