from data.solution_set_parser import SolutionSetParser
from plots.b5.final_tour_plot import FinalTourPlot
from plots.b5.length_plot import LengthPlot
from plots.c1.best_length_plot import BestLengthPlot

EXPERIMENTS_DIRECTORY = "../experiments"

baseline = SolutionSetParser.from_glob(f"{EXPERIMENTS_DIRECTORY}/baseline/*.json")
LengthPlot.write(baseline.results[0])
FinalTourPlot.write(baseline.results[0])

BestLengthPlot.write(
    SolutionSetParser.from_glob(f"{EXPERIMENTS_DIRECTORY}/c1_fixed_tenure/*.json")
)
