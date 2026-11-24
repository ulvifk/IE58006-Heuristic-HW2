package heuristic.localsearch;

import heuristic.model.solution.Solution;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/// Hands over a fixed list of moves, whatever the solution, and counts how many it handed over.
@RequiredArgsConstructor
class FakeMoveGenerator implements MoveGenerator<FakeMove> {

    private final List<FakeMove> moves;

    @Getter
    private int handedOverCount;

    @Override
    public void forEachMove(Solution solution, MoveConsumer<FakeMove> consumer) {
        for (var move : moves) {
            handedOverCount++;
            if (!consumer.accept(move)) return;
        }
    }
}
