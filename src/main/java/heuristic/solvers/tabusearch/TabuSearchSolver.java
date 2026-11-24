package heuristic.solvers.tabusearch;

import static heuristic.Constants.ZERO_TOLERANCE;

import heuristic.localsearch.BestMoveConsumer;
import heuristic.localsearch.MoveGenerator;
import heuristic.localsearch.TwoOptMove;
import heuristic.model.solution.Solution;
import heuristic.solvers.Solver;
import heuristic.solvers.SolverResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/// One tabu search run over the full 2-opt neighbourhood; solve returns the best solution and a log of every
/// selected move.
///
/// - each iteration applies the admissible move with the lowest score, even a worsening one; ties go to the
///   smallest (i, j)
/// - a move is tabu when either inserted edge is tabu; aspiration admits it when it beats the best-so-far length
/// - the selected move's removed edges become tabu for a tenure drawn from [minTenure, maxTenure]
public class TabuSearchSolver implements Solver {

    private final TabuSearchConfig config;
    private final MoveGenerator<TwoOptMove> moveGenerator;
    private final Random random;

    private final TabuList tabuList;
    private final EdgeInsertionCounts edgeInsertionCounts;

    private Solution current;
    private Solution best;

    private final List<IterationLog> iterationLogs; // [iteration - 1] -> the move that iteration selected

    public TabuSearchSolver(
            TabuSearchConfig config,
            MoveGenerator<TwoOptMove> moveGenerator,
            Random random,
            TabuList tabuList,
            EdgeInsertionCounts edgeInsertionCounts,
            Solution initialSolution) {
        this.config = config;
        this.moveGenerator = moveGenerator;
        this.random = random;

        this.tabuList = tabuList;
        this.edgeInsertionCounts = edgeInsertionCounts;

        this.current = initialSolution;
        this.best = initialSolution;
        this.iterationLogs = new ArrayList<>();
    }

    @Override
    public SolverResult solve() {
        for (int iteration = 1; iteration <= config.getMaxIterations(); iteration++) {
            var move = selectMove(iteration);
            var tabu = isTabu(move, iteration);
            var aspiring = isAspiring(move);

            current = move.apply();
            if (current.getCost() < best.getCost() - ZERO_TOLERANCE) {
                best = current;
            }

            updateShortTermMemory(move, iteration);
            updateLongTermMemory(move);

            log(iteration, move, tabu, aspiring);
        }

        return new SolverResult(best, iterationLogs);
    }

    // forbids reinserting the removed edges for a freshly drawn tenure
    private void updateShortTermMemory(TwoOptMove move, int iteration) {
        var tenure = random.nextInt(config.getMinTenure(), config.getMaxTenure() + 1);
        for (var edge : move.getRemovedEdges()) {
            tabuList.forbid(edge, iteration, tenure);
        }
    }

    private void updateLongTermMemory(TwoOptMove move) {
        for (var edge : move.getInsertedEdges()) {
            edgeInsertionCounts.addInsertion(edge);
        }
    }

    // fails when every move is tabu and none aspirates
    private TwoOptMove selectMove(int iteration) {
        var consumer = new BestMoveConsumer<TwoOptMove>(move -> isNotTabuOrAspirated(move, iteration), this::getScore);
        moveGenerator.forEachMove(current, consumer);

        return consumer.getBestMove().orElseThrow();
    }

    private boolean isNotTabuOrAspirated(TwoOptMove move, int iteration) {
        if (!isTabu(move, iteration)) return true;

        return isAspiring(move);
    }

    private boolean isTabu(TwoOptMove move, int iteration) {
        for (var edge : move.getInsertedEdges()) {
            if (tabuList.isTabu(edge, iteration)) return true;
        }

        return false;
    }

    private boolean isAspiring(TwoOptMove move) {
        return current.getCost() - move.getGain() < best.getCost() - ZERO_TOLERANCE;
    }

    // delta + F * (c(a, c) + c(b, d))
    private double getScore(TwoOptMove move) {
        var insertionCount = 0;
        for (var edge : move.getInsertedEdges()) {
            insertionCount += edgeInsertionCounts.getCount(edge);
        }

        return -move.getGain() + config.getFrequencyPenaltyWeight() * insertionCount;
    }

    private void log(int iteration, TwoOptMove move, boolean tabu, boolean aspiring) {
        iterationLogs.add(new IterationLog(
                iteration,
                move.getI(),
                move.getJ(),
                move.getRemovedEdges(),
                move.getInsertedEdges(),
                -move.getGain(),
                tabu,
                aspiring,
                current,
                best));
    }
}
