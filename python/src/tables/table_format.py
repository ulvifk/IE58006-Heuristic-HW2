from pathlib import Path
from typing import ClassVar

import pandas as pd


class TableFormat:
    """The number formatting and LaTeX layout every table shares, so all tables in the report look alike."""

    LENGTH_DECIMALS: ClassVar[int] = 2  # tour lengths and deltas

    # main.tex inputs every table from latex/tables
    LATEX_TABLES_DIRECTORY: ClassVar[Path] = (
        Path(__file__).parents[3] / "latex" / "tables"
    )

    @staticmethod
    def format_length(value: float) -> str:
        return f"{value:.{TableFormat.LENGTH_DECIMALS}f}"

    @staticmethod
    def format_flag(value: bool) -> str:
        return "yes" if value else "no"

    @staticmethod
    def stack_header(*lines: str) -> str:
        """A header cell over several lines; e.g. ("Candidate", "moves") -> \\makecell{Candidate\\\\moves}."""
        return "\\makecell{" + "\\\\".join(lines) + "}"

    @staticmethod
    def write(file_name: str, table: pd.DataFrame) -> None:
        """
        Writes an already formatted table as a booktabs tabular with every column
        centered, its column names as the header.
        """
        column_format = "c" * len(table.columns)
        latex = table.to_latex(index=False, escape=False, column_format=column_format)
        TableFormat.write_latex(file_name, latex)

    @staticmethod
    def write_latex(file_name: str, latex: str) -> None:
        """Writes latex/tables/<file_name>.tex, for main.tex to input."""
        TableFormat.LATEX_TABLES_DIRECTORY.mkdir(parents=True, exist_ok=True)
        (TableFormat.LATEX_TABLES_DIRECTORY / f"{file_name}.tex").write_text(latex)
