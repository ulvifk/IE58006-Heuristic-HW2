package heuristic.localsearch;

import heuristic.model.data.DistanceMatrix;
import heuristic.model.data.Edge;
import heuristic.model.data.Tour;
import heuristic.model.solution.Solution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/// Replaces edges (t[i], t[i+1]) and (t[j], t[j+1]) with (t[i], t[j]) and (t[i+1], t[j+1]) by reversing positions
/// i+1..j; position j+1 wraps to 0 when j is the last position.
///
/// e.g. i = 1, j = 3: [0, 1] -> [2, 3] -> [4, 5]  becomes  [0, 1] -> [3, 2] -> [4, 5]
@RequiredArgsConstructor
public class TwoOptMove extends Move {

    private final Solution solution;
    private final DistanceMatrix distanceMatrix;

    @Getter
    private final int i;

    @Getter
    private final int j;

    /// Feasible when it changes the tour:
    /// - j = i + 1 reverses a single city
    /// - i = 0 with j = last removes the two edges at the first city and adds them back, reversing the tour
    @Override
    public boolean isFeasible() {
        var lastPosition = solution.getTour().getCityIds().size() - 1;
        if (j - i < 2) return false;
        if (i == 0 && j == lastPosition) return false;

        return true;
    }

    @Override
    public Solution apply() {
        var cityIds = new ArrayList<>(solution.getTour().getCityIds());

        // subList is a view, so this reverses positions i+1..j in place
        Collections.reverse(cityIds.subList(i + 1, j + 1));

        return new Solution(new Tour(cityIds), solution.getCost() - getGain());
    }

    public List<Edge> getRemovedEdges() {
        var cityIds = solution.getTour().getCityIds();
        return List.of(
                new Edge(getCityAt(cityIds, i), getCityAt(cityIds, i + 1)),
                new Edge(getCityAt(cityIds, j), getCityAt(cityIds, j + 1)));
    }

    public List<Edge> getInsertedEdges() {
        var cityIds = solution.getTour().getCityIds();
        return List.of(
                new Edge(getCityAt(cityIds, i), getCityAt(cityIds, j)),
                new Edge(getCityAt(cityIds, i + 1), getCityAt(cityIds, j + 1)));
    }

    // removed minus inserted length, so the negative of delta
    @Override
    protected double computeGain() {
        var cityIds = solution.getTour().getCityIds();
        var cityAtI = getCityAt(cityIds, i);
        var cityAfterI = getCityAt(cityIds, i + 1);
        var cityAtJ = getCityAt(cityIds, j);
        var cityAfterJ = getCityAt(cityIds, j + 1);

        var removedLength =
                distanceMatrix.getDistance(cityAtI, cityAfterI) + distanceMatrix.getDistance(cityAtJ, cityAfterJ);
        var insertedLength =
                distanceMatrix.getDistance(cityAtI, cityAtJ) + distanceMatrix.getDistance(cityAfterI, cityAfterJ);

        return removedLength - insertedLength;
    }

    // wraps position j + 1 to the first city
    private static int getCityAt(List<Integer> cityIds, int position) {
        return cityIds.get(position % cityIds.size());
    }
}
