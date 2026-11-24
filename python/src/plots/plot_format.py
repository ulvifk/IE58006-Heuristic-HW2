from pathlib import Path
from typing import ClassVar

import matplotlib.pyplot as plt
import seaborn as sns
from matplotlib.figure import Figure

from data.solution_set import TabuSearchConfig


class PlotFormat:
    """
    The theme and output location every plot shares, so all figures in the report look
    alike; a light background with the series colors of the dark palette from the
    security-task-allocation overview.
    """

    COLORS: ClassVar[tuple[str, ...]] = ("#3987e5", "#d95926", "#199e70", "#c98500")

    FIGURE_WIDTH: ClassVar[float] = 6.5  # inches, the text width of a fullpage page
    DPI: ClassVar[int] = 300

    # main.tex includes every figure from latex/figures
    LATEX_FIGURES_DIRECTORY: ClassVar[Path] = (
        Path(__file__).parents[3] / "latex" / "figures"
    )

    @staticmethod
    def apply_theme() -> None:
        sns.set_theme(
            style="whitegrid", context="paper", palette=list(PlotFormat.COLORS)
        )

    @staticmethod
    def build_config_title(config: TabuSearchConfig) -> str:
        """The run's parameters as a plot title line; e.g. "d = 7, F = 0.00, 100 iterations" or "d drawn from [5, 10], F = 0.00, 100 iterations, seed 3"."""
        if config.min_tenure == config.max_tenure:
            return (
                f"$d$ = {config.min_tenure}, "
                f"$F$ = {config.frequency_penalty_weight:.2f}, "
                f"{config.max_iterations} iterations"
            )

        return (
            f"$d$ drawn from [{config.min_tenure}, {config.max_tenure}], "
            f"$F$ = {config.frequency_penalty_weight:.2f}, "
            f"{config.max_iterations} iterations, seed {config.seed}"
        )

    @staticmethod
    def save(file_name: str, figure: Figure) -> None:
        """Writes latex/figures/<file_name>.png, for main.tex to include, and closes the figure."""
        PlotFormat.LATEX_FIGURES_DIRECTORY.mkdir(parents=True, exist_ok=True)
        figure.savefig(
            PlotFormat.LATEX_FIGURES_DIRECTORY / f"{file_name}.png",
            dpi=PlotFormat.DPI,
            bbox_inches="tight",
        )
        plt.close(figure)
