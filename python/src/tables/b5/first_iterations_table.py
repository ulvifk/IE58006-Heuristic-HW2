from typing import ClassVar

import pandas as pd

from data.solution_set import Edge, ExperimentResult
from tables.table_format import TableFormat


class FirstIterationsTable:
    """B5: the first iterations of one run, one row per selected move with the lengths after it."""

    ROW_COUNT: ClassVar[int] = 15
    FILE_NAME: ClassVar[str] = "first_iterations"

    @staticmethod
    def write(result: ExperimentResult) -> None:
        rows = []
        for iteration_log in result.solver_result.iteration_logs[
            : FirstIterationsTable.ROW_COUNT
        ]:
            rows.append(
                {
                    "Iter.": iteration_log.iteration,
                    "$(i, j)$": f"({iteration_log.i}, {iteration_log.j})",
                    "Removed": FirstIterationsTable._format_edges(
                        iteration_log.removed_edges
                    ),
                    "Inserted": FirstIterationsTable._format_edges(
                        iteration_log.inserted_edges
                    ),
                    "$\\Delta$": TableFormat.format_length(iteration_log.delta),
                    "Tabu": TableFormat.format_flag(iteration_log.tabu),
                    "Aspiration": TableFormat.format_flag(iteration_log.aspiring),
                    "Current": TableFormat.format_length(iteration_log.current.cost),
                    "Best": TableFormat.format_length(iteration_log.best.cost),
                }
            )

        TableFormat.write(FirstIterationsTable.FILE_NAME, pd.DataFrame(rows))

    @staticmethod
    def _format_edges(edges: tuple[Edge, ...]) -> str:
        """e.g. (10, 16), (11, 17) -> "(10,16) (11,17)"."""
        return " ".join(
            f"({edge.first_city_id},{edge.second_city_id})" for edge in edges
        )
