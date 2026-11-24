# IE 58006 Heuristic Optimization, Assignment 2: Tabu Search

Tabu search with 2-opt moves for a 20-city Euclidean TSP, the experiments the
assignment asks for, and the report built from their results.

The tabu search and the experiments are implemented in Java, under `src/main/java`.
Python, under `python/`, is used only for analysis and plotting: it reads the results
Java writes and turns them into the report's tables and figures.

The project has three parts that run in order:

1. Java solves every run and writes one JSON file per run to `experiments/`.
2. Python reads those files and writes the report's tables and figures into `latex/`.
3. LaTeX builds the report from `latex/main.tex`. Part A is a handwritten page,
   `latex/part_a_handwritten.pdf`, which the report includes.

## Requirements

- JDK 27. Gradle downloads it through the toolchain resolver if it is not installed.
- [uv](https://docs.astral.sh/uv/) for the Python analysis (Python 3.14).
- A TeX distribution with `latexmk` and `pdflatex`.

## Running

```sh
# 1. Run the baseline and every Part C experiment; writes experiments/<experiment>/*.json
./gradlew run

# 2. Write the tables and figures into latex/tables and latex/figures
cd python
uv run python src/write_tables.py
uv run python src/write_plots.py

# 3. Build the report into latex/build/main.pdf
cd ../latex
latexmk main.tex
```

Every run starts from the same initial tour and stops after 100 iterations:

- `baseline`: fixed tenure d = 7.
- `c1_fixed_tenure`: d in {0, 1, 3, 5, 7, 10, 15, 20}.
- `c2_random_tenure`: tenure drawn from [2, 5], [5, 10], [7, 14] or [10, 18], each
  with seeds 0 to 9.
- `c3_frequency_penalty`: d = 0 with F in {0, 0.05, 0.10, 0.20, 0.40}.

Runs are reproducible. Only the `c2_random_tenure` runs use randomness, for the tenure
draws, and each of them carries its own seed. The other runs are deterministic.

## Algorithm

- A tour is a permutation of the 20 cities, closed back to its first city. Distances
  are Euclidean, rounded to two decimals.
- Each iteration scans every valid 2-opt move on the current tour and evaluates it from
  the four edges it changes:
  $\Delta = d(t_i, t_j) + d(t_{i+1}, t_{j+1}) - d(t_i, t_{i+1}) - d(t_j, t_{j+1})$.
  A move reverses positions $i+1, \dots, j$. Moves with $j = i + 1$ and the reversal
  of the full tour are skipped, because they do not change the tour.
- The admissible move with the lowest score is applied, even if it makes the tour
  longer. Ties go to the smallest $(i, j)$.
- The two edges a move removes become tabu. An edge removed at iteration $k$ with
  tenure $d$ is tabu during iterations $k+1, \dots, k+d$. A move is tabu if either of
  the two edges it inserts is tabu.
- Aspiration: a tabu move is still admissible if its tour length is strictly below the
  best-so-far length.
- The score of a move is $\Delta + F \cdot (c(a,c) + c(b,d))$, where $(a,c)$ and
  $(b,d)$ are the inserted edges and $c$ counts how often an edge has been inserted by
  a selected move. With $F = 0$ the score is $\Delta$. Reported tour lengths are always
  true distances, without the penalty.
- The best-so-far tour is kept separately and only replaced by a strictly better one.
- Each best tour passes the feasibility checks before its run is written.

## Development

```sh
./gradlew formatAndCheck            # format Java, compile, test and run SpotBugs
cd python && uv run ruff format . && uv run ruff check . && uv run pyrefly check
```
