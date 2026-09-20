package task;

import java.util.Random;

public final class ThreeWayPartition {
    private ThreeWayPartition() {}

    public static Bounds partition(int[] values, int left, int right,
                                   AlgorithmMetrics metrics, Random random) {
        int pivotIndex = left + random.nextInt(right - left + 1);
        int pivot = values[pivotIndex];

        int lt = left;
        int i = left;
        int gt = right;

        while (i <= gt) {
            metrics.addComparison();

            if (values[i] < pivot) {
                swap(values, lt, i);
                lt++;
                i++;
            } else {
                metrics.addComparison();

                if (values[i] > pivot) {
                    swap(values, i, gt);
                    gt--;
                } else {
                    i++;
                }
            }
        }

        return new Bounds(lt, gt);
    }

    static void swap(int[] values, int a, int b) {
        int temp = values[a];
        values[a] = values[b];
        values[b] = temp;
    }

    public record Bounds(int equalStart, int equalEnd) {}
}
