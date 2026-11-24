package heuristic.experiments;

import heuristic.solvers.tabusearch.TabuSearchConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/// The runs of each experiment, all with lmax = 100. A fixed tenure d is the range [d, d]; runs without random
/// tenure are deterministic, so they use seed 0 only.
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExperimentsConfigs {

    private static final int MAX_ITERATIONS = 100;

    /// B5: d = 7, no frequency penalty; 1 run.
    public static List<TabuSearchConfig> getBaselineConfigs() {
        return List.of(new TabuSearchConfig(MAX_ITERATIONS, 7, 7, 0.0, 0));
    }

    /// C1: d in {0, 1, 3, 5, 7, 10, 15, 20}; 8 runs.
    public static List<TabuSearchConfig> getFixedTenureConfigs() {
        var tenures = List.of(0, 1, 3, 5, 7, 10, 15, 20);

        var configs = new ArrayList<TabuSearchConfig>();
        for (var tenure : tenures) {
            configs.add(new TabuSearchConfig(MAX_ITERATIONS, tenure, tenure, 0.0, 0));
        }

        return configs;
    }

    /// C2: tenure drawn from [2, 5], [5, 10], [7, 14] or [10, 18]; 4 ranges x seeds 0..9.
    public static List<TabuSearchConfig> getRandomTenureConfigs() {
        var minTenures = List.of(2, 5, 7, 10);
        var maxTenures = List.of(5, 10, 14, 18);
        var seedCount = 10;

        var configs = new ArrayList<TabuSearchConfig>();
        for (int range = 0; range < minTenures.size(); range++) {
            for (long seed = 0; seed < seedCount; seed++) {
                configs.add(
                        new TabuSearchConfig(MAX_ITERATIONS, minTenures.get(range), maxTenures.get(range), 0.0, seed));
            }
        }

        return configs;
    }

    /// C3: d = 0 and F in {0, 0.05, 0.10, 0.20, 0.40}; 5 runs.
    public static List<TabuSearchConfig> getFrequencyPenaltyConfigs() {
        var frequencyPenaltyWeights = List.of(0.0, 0.05, 0.10, 0.20, 0.40);

        var configs = new ArrayList<TabuSearchConfig>();
        for (var frequencyPenaltyWeight : frequencyPenaltyWeights) {
            configs.add(new TabuSearchConfig(MAX_ITERATIONS, 0, 0, frequencyPenaltyWeight, 0));
        }

        return configs;
    }
}
