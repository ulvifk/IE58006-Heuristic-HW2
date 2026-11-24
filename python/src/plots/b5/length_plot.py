from typing import ClassVar

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

from data.solution_set import ExperimentResult
from plots.plot_format import PlotFormat


class LengthPlot:
    """B5: the current and best-so-far tour length of one run over its iterations."""

    FILE_NAME: ClassVar[str] = "length"

    @staticmethod
    def write(result: ExperimentResult) -> None:
        lengths = []
        for iteration_log in result.solver_result.iteration_logs:
            lengths.append(
                {
                    "Iteration": iteration_log.iteration,
                    "Length": iteration_log.current.cost,
                    "Tour": "Current",
                }
            )
            lengths.append(
                {
                    "Iteration": iteration_log.iteration,
                    "Length": iteration_log.best.cost,
                    "Tour": "Best so far",
                }
            )

        PlotFormat.apply_theme()
        figure, axes = plt.subplots(figsize=(PlotFormat.FIGURE_WIDTH, 3.5))
        sns.lineplot(
            data=pd.DataFrame(lengths),
            x="Iteration",
            y="Length",
            hue="Tour",
            ax=axes,
        )
        axes.legend(title=None)
        axes.set_title(PlotFormat.build_config_title(result.config))

        PlotFormat.save(LengthPlot.FILE_NAME, figure)
