package heuristic.solvers.tabusearch;

import heuristic.model.data.Edge;

/// Long-term memory: how many times each undirected edge has been inserted by a selected move.
public class EdgeInsertionCounts {

    private final int[][] counts; // [cityId][cityId] -> insertions of the edge so far

    public EdgeInsertionCounts(int cityCount) {
        this.counts = new int[cityCount][cityCount];
    }

    public void addInsertion(Edge edge) {
        counts[edge.getFirstCityId()][edge.getSecondCityId()]++;
        counts[edge.getSecondCityId()][edge.getFirstCityId()]++;
    }

    public int getCount(Edge edge) {
        return counts[edge.getFirstCityId()][edge.getSecondCityId()];
    }
}
