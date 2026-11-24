package heuristic.solvers.tabusearch;

import java.util.Locale;
import lombok.Value;

@Value
public class TabuSearchConfig {

    int maxIterations; // lmax

    int minTenure; // tenure drawn uniformly from [minTenure, maxTenure] per selected move
    int maxTenure;

    double frequencyPenaltyWeight; // F in score = delta + F * (c(a, c) + c(b, d))

    long seed; // for the tenure draws

    // the parameters that differ between experiment runs; e.g. tenure-5-10_penalty-0.00_seed-3
    public String getConfigKey() {
        return String.format(
                Locale.ROOT, "tenure-%d-%d_penalty-%.2f_seed-%d", minTenure, maxTenure, frequencyPenaltyWeight, seed);
    }
}
