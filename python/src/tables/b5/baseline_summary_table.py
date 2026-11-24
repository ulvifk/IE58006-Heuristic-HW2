from typing import ClassVar

import pandas as pd

from data.run_statistics import RunStatistics
from data.solution_set import ExperimentResult
from tables.table_format import TableFormat


class BaselineSummaryTable:
    """B5: one row for the run: final best length, worsening moves selected and aspiration uses."""

    FILE_NAME: ClassVar[str] = "baseline_summary"

    @staticmethod
    def write(result: ExperimentResult) -> None:
        table = pd.DataFrame(
            [
                {
                    "Best": TableFormat.format_length(
                        result.solver_result.best_solution.cost
                    ),
                    TableFormat.stack_header(
                        "Worsening", "moves"
                    ): RunStatistics.get_worsening_move_count(result),
                    TableFormat.stack_header(
                        "Aspiration", "uses"
                    ): RunStatistics.get_aspiration_count(result),
                }
            ]
        )
        TableFormat.write(BaselineSummaryTable.FILE_NAME, table)
