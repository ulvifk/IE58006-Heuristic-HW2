from dataclasses import dataclass


@dataclass(frozen=True, slots=True)
class City:
    id: int
    x: float
    y: float


@dataclass(frozen=True, slots=True)
class DistanceMatrix:
    distances: tuple[
        tuple[float, ...], ...
    ]  # [from city id][to city id] -> rounded distance


@dataclass(frozen=True, slots=True)
class TabuSearchConfig:
    max_iterations: int  # lmax

    min_tenure: int  # tenure drawn uniformly from [min_tenure, max_tenure]
    max_tenure: int

    frequency_penalty_weight: float  # F in score = delta + F * (c(a, c) + c(b, d))

    seed: int
    config_key: str  # e.g. tenure-5-10_penalty-0.00_seed-3


@dataclass(frozen=True, slots=True)
class Tour:
    city_ids: tuple[
        int, ...
    ]  # [position] -> city id; the return to the first city is implied


@dataclass(frozen=True, slots=True)
class Solution:
    tour: Tour
    cost: float  # closed-tour length, including the return to the first city


@dataclass(frozen=True, slots=True)
class Edge:
    first_city_id: int
    second_city_id: int


@dataclass(frozen=True, slots=True)
class IterationLog:
    iteration: int  # 1 for the first selected move

    i: int  # the 2-opt move's positions
    j: int
    removed_edges: tuple[Edge, ...]
    inserted_edges: tuple[Edge, ...]
    delta: float  # tour length change; positive for a worsening move

    tabu: bool  # an inserted edge was tabu
    aspiring: bool  # its length beats the best-so-far, so aspiration would admit it even if tabu

    current: Solution  # after the move
    best: Solution


@dataclass(frozen=True, slots=True)
class SolverResult:
    best_solution: Solution
    iteration_logs: tuple[
        IterationLog, ...
    ]  # [iteration - 1] -> the move that iteration selected


@dataclass(frozen=True, slots=True)
class ProblemData:
    cities: tuple[City, ...]  # [city id] -> city
    distance_matrix: DistanceMatrix


@dataclass(frozen=True, slots=True)
class ExperimentResult:
    problem_data: ProblemData
    config: TabuSearchConfig
    solver_result: SolverResult


@dataclass(frozen=True, slots=True)
class SolutionSet:
    results: tuple[ExperimentResult, ...]  # one per run file, ordered by file path
