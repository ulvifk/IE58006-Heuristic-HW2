from typing import ClassVar

import pandas as pd

from data.solution_set import SolutionSet
from tables.table_format import TableFormat


class RandomTenureTable:
    """C2: one row per tenure range, with the best, mean and sample standard deviation of the final best length over its seeds."""

    FILE_NAME: ClassVar[str] = "c2_random_tenure"

    @staticmethod
    def write(solution_set: SolutionSet) -> None:
        runs = []
        for result in solution_set.results:
            runs.append(
                {
                    "min_tenure": result.config.min_tenure,
                    "max_tenure": result.config.max_tenure,
                    "best_cost": result.solver_result.best_solution.cost,
                }
            )
        statistics = (
            pd.DataFrame(runs)
            .groupby(["min_tenure", "max_tenure"])
            .agg(
                best=("best_cost", "min"),
                mean=("best_cost", "mean"),
                standard_deviation=("best_cost", "std"),
            )
            .reset_index()
        )

        table = pd.DataFrame(
            {
                # in math mode, since a row starting with "[" would read as \\[length]
                "$[d_{min}, d_{max}]$": "$["
                + statistics["min_tenure"].astype(str)
                + ", "
                + statistics["max_tenure"].astype(str)
                + "]$",
                "Best": statistics["best"].map(TableFormat.format_length),
                "Mean": statistics["mean"].map(TableFormat.format_length),
                "Std. dev.": statistics["standard_deviation"].map(
                    TableFormat.format_length
                ),
            }
        )
        TableFormat.write(RandomTenureTable.FILE_NAME, table)
