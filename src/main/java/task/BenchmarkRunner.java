package task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

public class BenchmarkRunner {
    private static final int[] SIZES = {1_000, 10_000, 100_000, 1_000_000};
    private static final String[] INPUTS = {"random", "sorted", "duplicates"};
    private static final int REPEATS = 5;

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        warmUp();

        try (PrintWriter out = new PrintWriter(new FileWriter("results.csv"))) {
            out.println("algorithm,input,n,time_ms,comparisons,max_depth");

            for (String input : INPUTS) {
                for (int n : SIZES) {
                    int[] source = createInput(input, n);
                    runCase("mergesort", input, n, source, out);
                    runCase("quicksort", input, n, source, out);
                    runCase("quickselect", input, n, source, out);
                }
            }
        }

        System.out.println("Created results.csv");
    }

    private static void runCase(String algorithm, String input, int n,
                                int[] source, PrintWriter out) {
        Measurement[] runs = new Measurement[REPEATS];

        for (int i = 0; i < REPEATS; i++) {
            int[] data = source.clone();
            AlgorithmMetrics metrics = new AlgorithmMetrics();

            switch (algorithm) {
                case "mergesort" -> MergeSorter.sort(data, metrics);
                case "quicksort" -> SafeQuickSort.sort(data, metrics);
                case "quickselect" -> QuickSelector.select(data, n / 2, metrics);
                default -> throw new IllegalArgumentException("Unknown algorithm");
            }

            runs[i] = new Measurement(metrics.timeMs(), metrics.comparisons(), metrics.maxDepth());
        }

        Arrays.sort(runs, Comparator.comparingDouble(Measurement::timeMs));
        Measurement median = runs[REPEATS / 2];

        out.printf(Locale.US, "%s,%s,%d,%.6f,%d,%d%n",
                algorithm, input, n, median.timeMs(), median.comparisons(), median.maxDepth());

        System.out.printf(Locale.US, "%-12s %-10s n=%-8d time=%8.3f ms%n",
                algorithm, input, n, median.timeMs());
    }

    private static int[] createInput(String type, int n) {
        int[] data = new int[n];
        Random random = new Random(20260920L + n + type.hashCode());

        switch (type) {
            case "sorted" -> {
                for (int i = 0; i < n; i++) data[i] = i;
            }
            case "duplicates" -> {
                for (int i = 0; i < n; i++) data[i] = random.nextInt(10);
            }
            case "random" -> {
                for (int i = 0; i < n; i++) data[i] = random.nextInt();
            }
            default -> throw new IllegalArgumentException("Unknown input type");
        }

        return data;
    }

    private static void warmUp() {
        int[] source = createInput("random", 20_000);

        for (int i = 0; i < 2; i++) {
            MergeSorter.sort(source.clone(), new AlgorithmMetrics());
            SafeQuickSort.sort(source.clone(), new AlgorithmMetrics());
            QuickSelector.select(source.clone(), source.length / 2, new AlgorithmMetrics());
        }
    }

    private record Measurement(double timeMs, long comparisons, int maxDepth) {}
}
