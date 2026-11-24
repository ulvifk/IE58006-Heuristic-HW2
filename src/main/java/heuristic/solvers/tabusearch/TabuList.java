package heuristic.solvers.tabusearch;

import heuristic.model.data.Edge;

/// Short-term memory of removed edges: an edge removed at iteration k with tenure d may not be inserted again during
/// iterations k+1..k+d, and is allowed again from k+d+1.
///
/// e.g. removed at iteration 12 with d = 5 -> tabu at 13, 14, 15, 16, 17; allowed again at 18
public class TabuList {

    private final int[][] tabuUntil; // [cityId][cityId] -> the edge is tabu until this iteration, inclusive

    public TabuList(int cityCount) {
        this.tabuUntil = new int[cityCount][cityCount];
    }

    /// Forbids reinserting the edge for the next tenure iterations; tenure 0 forbids nothing.
    public void forbid(Edge edge, int iteration, int tenure) {
        var tabuUntilIteration = iteration + tenure;
        tabuUntil[edge.getFirstCityId()][edge.getSecondCityId()] = tabuUntilIteration;
        tabuUntil[edge.getSecondCityId()][edge.getFirstCityId()] = tabuUntilIteration;
    }

    public boolean isTabu(Edge edge, int iteration) {
        return iteration <= tabuUntil[edge.getFirstCityId()][edge.getSecondCityId()];
    }
}
