from typing import ClassVar

from data.solution_set import ExperimentResult, Tour


class RunStatistics:
    """The per-run counts the B5 and Part C tables report, read from the iteration log."""

    ZERO_TOLERANCE: ClassVar[float] = 1e-6  # the solver's tolerance on deltas

    @staticmethod
    def get_worsening_move_count(result: ExperimentResult) -> int:
        count = 0
        for iteration_log in result.solver_result.iteration_logs:
            if iteration_log.delta > RunStatistics.ZERO_TOLERANCE:
                count += 1

        return count

    @staticmethod
    def get_aspiration_count(result: ExperimentResult) -> int:
        """Selected moves that aspiration actually let through: tabu and aspiring."""
        count = 0
        for iteration_log in result.solver_result.iteration_logs:
            if not iteration_log.tabu:
                continue
            if not iteration_log.aspiring:
                continue

            count += 1

        return count

    @staticmethod
    def get_distinct_tour_count(result: ExperimentResult) -> int:
        """Distinct tours among the initial tour and every current tour after it."""
        return len(RunStatistics._get_visited_tour_keys(result))

    @staticmethod
    def get_repeated_visit_count(result: ExperimentResult) -> int:
        """Iterations whose current tour was already visited, the initial tour included."""
        visit_count = len(result.solver_result.iteration_logs) + 1

        return visit_count - RunStatistics.get_distinct_tour_count(result)

    @staticmethod
    def _get_visited_tour_keys(result: ExperimentResult) -> set[tuple[int, ...]]:
        iteration_logs = result.solver_result.iteration_logs
        first_log = iteration_logs[0]

        # the tour before iteration 1, rebuilt by undoing its reversal of i+1..j
        initial_city_ids = list(first_log.current.tour.city_ids)
        initial_city_ids[first_log.i + 1 : first_log.j + 1] = reversed(
            initial_city_ids[first_log.i + 1 : first_log.j + 1]
        )

        keys = {RunStatistics._get_tour_key(Tour(city_ids=tuple(initial_city_ids)))}
        for iteration_log in iteration_logs:
            keys.add(RunStatistics._get_tour_key(iteration_log.current.tour))

        return keys

    @staticmethod
    def _get_tour_key(tour: Tour) -> tuple[int, ...]:
        """
        The same key for both directions of a closed tour; every tour starts at the
        same city, so the key keeps the direction with the smaller second city;
        e.g. (0, 3, 1, 2) and (0, 2, 1, 3) -> (0, 2, 1, 3).
        """
        city_ids = tour.city_ids
        reversed_city_ids = (city_ids[0], *reversed(city_ids[1:]))

        return min(city_ids, reversed_city_ids)
