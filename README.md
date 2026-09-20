# DAA Assignment 1 - Full Reference Project

Java 17 + Maven project for Divide and Conquer algorithms.

## Included

- MergeSort with one reusable buffer
- Insertion Sort cutoff at 15 elements
- QuickSort with random pivot
- 3-way partition for duplicates
- smaller-side-first recursion with loop on the larger side
- QuickSelect reusing the same partition
- Metrics: comparisons, maximum recursion depth, time
- Benchmark with 4 sizes, 3 input types, 5 repetitions and median result
- JUnit 5 tests
- PlotGenerator.java creating the three required PNG files

## Run tests

```bash
mvn clean test
```

## Run benchmark

Run `task.BenchmarkRunner` in IntelliJ IDEA.

It creates:

`results.csv`

## Create plots

Run `task.PlotGenerator` after the benchmark.

It creates:

- `time_vs_n.png`
- `depth_vs_n.png`
- `ratio_vs_n.png`

## CSV columns

`algorithm,input,n,time_ms,comparisons,max_depth`

## Git requirement

Before submission, create the required feature branches and add a `v1.0` tag.
