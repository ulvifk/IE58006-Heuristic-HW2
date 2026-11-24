import glob
import json
from typing import Any

from data.solution_set import (
    City,
    DistanceMatrix,
    Edge,
    ExperimentResult,
    IterationLog,
    ProblemData,
    Solution,
    SolutionSet,
    SolverResult,
    TabuSearchConfig,
    Tour,
)


class SolutionSetParser:
    @staticmethod
    def from_glob(pattern: str) -> SolutionSet:
        """
        Reads every run file matching a glob pattern, ordered by path; e.g.
        "../experiments/c1_fixed_tenure/*.json" or "../experiments/*/tenure-7-7_*.json".
        """
        results = []
        for path in sorted(glob.glob(pattern)):
            with open(path) as file:
                results.append(
                    SolutionSetParser._parse_experiment_result(json.load(file))
                )

        return SolutionSet(results=tuple(results))

    @staticmethod
    def _parse_experiment_result(raw: dict[str, Any]) -> ExperimentResult:
        raw_solver_result = raw["solver_result"]
        iteration_logs = tuple(
            SolutionSetParser._parse_iteration_log(raw_log)
            for raw_log in raw_solver_result["iteration_logs"]
        )

        return ExperimentResult(
            problem_data=SolutionSetParser._parse_problem_data(raw["problem_data"]),
            config=TabuSearchConfig(**raw["config"]),
            solver_result=SolverResult(
                best_solution=SolutionSetParser._parse_solution(
                    raw_solver_result["best_solution"]
                ),
                iteration_logs=iteration_logs,
            ),
        )

    @staticmethod
    def _parse_problem_data(raw: dict[str, Any]) -> ProblemData:
        distances = tuple(tuple(row) for row in raw["distance_matrix"]["distances"])

        return ProblemData(
            cities=tuple(City(**raw_city) for raw_city in raw["cities"]),
            distance_matrix=DistanceMatrix(distances=distances),
        )

    @staticmethod
    def _parse_iteration_log(raw: dict[str, Any]) -> IterationLog:
        return IterationLog(
            iteration=raw["iteration"],
            i=raw["i"],
            j=raw["j"],
            removed_edges=tuple(Edge(**raw_edge) for raw_edge in raw["removed_edges"]),
            inserted_edges=tuple(
                Edge(**raw_edge) for raw_edge in raw["inserted_edges"]
            ),
            delta=raw["delta"],
            tabu=raw["tabu"],
            aspiring=raw["aspiring"],
            current=SolutionSetParser._parse_solution(raw["current"]),
            best=SolutionSetParser._parse_solution(raw["best"]),
        )

    @staticmethod
    def _parse_solution(raw: dict[str, Any]) -> Solution:
        return Solution(
            tour=Tour(city_ids=tuple(raw["tour"]["city_ids"])),
            cost=raw["cost"],
        )
