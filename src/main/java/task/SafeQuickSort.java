package task;

import java.util.Random;

public final class SafeQuickSort {
    private SafeQuickSort() {}

    public static void sort(int[] values, AlgorithmMetrics metrics) {
        metrics.reset();
        metrics.startTimer();

        if (values.length > 1) {
            sortRange(values, 0, values.length - 1, metrics, new Random(), 1);
        }

        metrics.stopTimer();
    }

    private static void sortRange(int[] values, int left, int right,
                                  AlgorithmMetrics metrics, Random random, int depth) {
        while (left < right) {
            metrics.visitDepth(depth);

            ThreeWayPartition.Bounds bounds = ThreeWayPartition.partition(
                    values, left, right, metrics, random
            );

            int lt = bounds.equalStart();
            int gt = bounds.equalEnd();

            int leftSize = lt - left;
            int rightSize = right - gt;

            if (leftSize < rightSize) {
                if (left < lt - 1) {
                    sortRange(values, left, lt - 1, metrics, random, depth + 1);
                }
                left = gt + 1;
            } else {
                if (gt + 1 < right) {
                    sortRange(values, gt + 1, right, metrics, random, depth + 1);
                }
                right = lt - 1;
            }
        }
    }
}
