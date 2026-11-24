package heuristic;

import heuristic.experiments.ExperimentResult;
import heuristic.experiments.ExperimentResultParser;
import heuristic.experiments.ExperimentsConfigs;
import heuristic.feasibility.FeasibilityValidation;
import heuristic.model.data.ProblemData;
import heuristic.model.data.ProblemDataParser;
import heuristic.solvers.tabusearch.TabuSearchConfig;
import heuristic.solvers.tabusearch.TabuSearchSolverFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Runs every experiment and writes one JSON file per run to experiments/<experiment>/, for the analysis script.
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final Path INSTANCE_PATH = Path.of("instances", "tsp20.json");
    private static final Path OUTPUT_DIRECTORY = Path.of("experiments");

    public static void main(String[] args) throws IOException {
        var problemData = ProblemDataParser.fromJson(Files.readString(INSTANCE_PATH));

        runExperiment("baseline", ExperimentsConfigs.getBaselineConfigs(), problemData);
        runExperiment("c1_fixed_tenure", ExperimentsConfigs.getFixedTenureConfigs(), problemData);
        runExperiment("c2_random_tenure", ExperimentsConfigs.getRandomTenureConfigs(), problemData);
        runExperiment("c3_frequency_penalty", ExperimentsConfigs.getFrequencyPenaltyConfigs(), problemData);
    }

    private static void runExperiment(String experimentName, List<TabuSearchConfig> configs, ProblemData problemData)
            throws IOException {
        var directory = OUTPUT_DIRECTORY.resolve(experimentName);
        Files.createDirectories(directory);

        for (var config : configs) {
            var solverResult =
                    TabuSearchSolverFactory.build(config, problemData).solve();
            FeasibilityValidation.validate(solverResult.getBestSolution(), problemData);

            var json = ExperimentResultParser.toJson(new ExperimentResult(problemData, config, solverResult));
            Files.writeString(directory.resolve(config.getConfigKey() + ".json"), json);
        }

        LOGGER.info("{}: {} runs written to {}", experimentName, configs.size(), directory);
    }
}
