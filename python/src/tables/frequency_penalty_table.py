from typing import ClassVar

import pandas as pd

from data.run_statistics import RunStatistics
from data.solution_set import SolutionSet
from tables.table_format import TableFormat


class FrequencyPenaltyTable:
    """C3: one row per frequency penalty weight F, ordered by F, with the run's best length and distinct tours."""

    FILE_NAME: ClassVar[str] = "c3_frequency_penalty"

    @staticmethod
    def write(solution_set: SolutionSet) -> None:
        results = sorted(
            solution_set.results,
            key=lambda result: result.config.frequency_penalty_weight,
        )

        rows = []
        for result in results:
            rows.append(
                {
                    "$F$": TableFormat.format_length(
                        result.config.frequency_penalty_weight
                    ),
                    "Best": TableFormat.format_length(
                        result.solver_result.best_solution.cost
                    ),
                    TableFormat.stack_header(
                        "Distinct", "tours"
                    ): RunStatistics.get_distinct_tour_count(result),
                }
            )

        TableFormat.write(FrequencyPenaltyTable.FILE_NAME, pd.DataFrame(rows))
