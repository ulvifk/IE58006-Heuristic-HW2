package heuristic.solvers.tabusearch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import heuristic.model.data.Edge;
import org.junit.jupiter.api.Test;

class TabuListTest {

    // part A1(c): executed at iteration 12 with d = 5
    @Test
    void forbidKeepsTheEdgeTabuForTheNextTenureIterations() {
        var tabuList = new TabuList(20);

        tabuList.forbid(new Edge(10, 3), 12, 5);

        for (int iteration = 13; iteration <= 17; iteration++) {
            assertTrue(tabuList.isTabu(new Edge(10, 3), iteration));
        }
        assertFalse(tabuList.isTabu(new Edge(10, 3), 18));
    }

    @Test
    void isTabuIgnoresTheEdgeDirection() {
        var tabuList = new TabuList(20);

        tabuList.forbid(new Edge(10, 3), 12, 5);

        assertTrue(tabuList.isTabu(new Edge(3, 10), 13));
    }

    @Test
    void forbidWithTenureZeroForbidsNothing() {
        var tabuList = new TabuList(20);

        tabuList.forbid(new Edge(10, 3), 12, 0);

        assertFalse(tabuList.isTabu(new Edge(10, 3), 13));
    }

    @Test
    void anEdgeNeverRemovedIsNotTabu() {
        var tabuList = new TabuList(20);

        tabuList.forbid(new Edge(10, 3), 12, 5);

        assertFalse(tabuList.isTabu(new Edge(2, 9), 13));
    }
}
