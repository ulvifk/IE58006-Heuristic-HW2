from data.solution_set_parser import SolutionSetParser
from tables.b5.baseline_summary_table import BaselineSummaryTable
from tables.b5.final_best_tour import FinalBestTour
from tables.b5.first_iterations_table import FirstIterationsTable
from tables.fixed_tenure_table import FixedTenureTable
from tables.frequency_penalty_table import FrequencyPenaltyTable
from tables.random_tenure_table import RandomTenureTable

EXPERIMENTS_DIRECTORY = "../experiments"

baseline = SolutionSetParser.from_glob(f"{EXPERIMENTS_DIRECTORY}/baseline/*.json")
FirstIterationsTable.write(baseline.results[0])
FinalBestTour.write(baseline.results[0])
BaselineSummaryTable.write(baseline.results[0])

FixedTenureTable.write(
    SolutionSetParser.from_glob(f"{EXPERIMENTS_DIRECTORY}/c1_fixed_tenure/*.json")
)
RandomTenureTable.write(
    SolutionSetParser.from_glob(f"{EXPERIMENTS_DIRECTORY}/c2_random_tenure/*.json")
)
FrequencyPenaltyTable.write(
    SolutionSetParser.from_glob(f"{EXPERIMENTS_DIRECTORY}/c3_frequency_penalty/*.json")
)
