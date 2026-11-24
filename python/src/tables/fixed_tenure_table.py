from typing import ClassVar

import pandas as pd

from data.run_statistics import RunStatistics
from data.solution_set import SolutionSet
from tables.table_format import TableFormat


class FixedTenureTable:
    """C1: one row per fixed tenure d, ordered by d, with the run's best length and counts."""

    FILE_NAME: ClassVar[str] = "c1_fixed_tenure"

    @staticmethod
    def write(solution_set: SolutionSet) -> None:
        results = sorted(
            solution_set.results, key=lambda result: result.config.min_tenure
        )

        rows = []
        for result in results:
            rows.append(
                {
                    "$d$": result.config.min_tenure,
                    "Best": TableFormat.format_length(
                        result.solver_result.best_solution.cost
                    ),
                    TableFormat.stack_header(
                        "Worsening", "moves"
                    ): RunStatistics.get_worsening_move_count(result),
                    TableFormat.stack_header(
                        "Aspiration", "uses"
                    ): RunStatistics.get_aspiration_count(result),
                    TableFormat.stack_header(
                        "Repeated", "tours"
                    ): RunStatistics.get_repeated_visit_count(result),
                }
            )

        TableFormat.write(FixedTenureTable.FILE_NAME, pd.DataFrame(rows))
