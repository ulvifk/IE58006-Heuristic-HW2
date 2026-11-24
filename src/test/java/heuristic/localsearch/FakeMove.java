package heuristic.localsearch;

import heuristic.model.data.Tour;
import heuristic.model.solution.Solution;
import java.util.List;
import lombok.Getter;

/// A move with a fixed feasibility and gain that applies to its own result, for testing without a real tour.
class FakeMove extends Move {

    @Getter
    private final Solution result = new Solution(new Tour(List.of()), 0.0);

    private final boolean feasible;
    private final double fakeGain;

    FakeMove(boolean feasible, double gain) {
        this.feasible = feasible;
        this.fakeGain = gain;
    }

    @Override
    public boolean isFeasible() {
        return feasible;
    }

    @Override
    public Solution apply() {
        return result;
    }

    @Override
    protected double computeGain() {
        return fakeGain;
    }
}
