from typing import ClassVar

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

from data.solution_set import SolutionSet
from plots.plot_format import PlotFormat


class BestLengthPlot:
    """C1: the final best length of each fixed tenure d, against d."""

    FILE_NAME: ClassVar[str] = "c1_best_length"

    @staticmethod
    def write(solution_set: SolutionSet) -> None:
        rows = []
        for result in solution_set.results:
            rows.append(
                {
                    "Tabu duration $d$": result.config.min_tenure,
                    "Best length": result.solver_result.best_solution.cost,
                }
            )
        best_lengths = pd.DataFrame(rows).sort_values("Tabu duration $d$")

        PlotFormat.apply_theme()
        figure, axes = plt.subplots(figsize=(PlotFormat.FIGURE_WIDTH, 3.0))
        sns.lineplot(
            data=best_lengths,
            x="Tabu duration $d$",
            y="Best length",
            marker="o",
            ax=axes,
        )
        axes.set_xticks(best_lengths["Tabu duration $d$"])
        axes.set_title(
            f"{solution_set.results[0].config.max_iterations} iterations, no frequency penalty"
        )

        PlotFormat.save(BestLengthPlot.FILE_NAME, figure)
