package task;

import java.util.Random;

public final class QuickSelector {
    private QuickSelector() {}

    public static int select(int[] values, int k, AlgorithmMetrics metrics) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Array must not be empty");
        }
        if (k < 0 || k >= values.length) {
            throw new IllegalArgumentException("k must be in [0, n-1]");
        }

        metrics.reset();
        metrics.startTimer();
        metrics.visitDepth(1);

        int left = 0;
        int right = values.length - 1;
        Random random = new Random();

        while (left <= right) {
            if (left == right) {
                metrics.stopTimer();
                return values[left];
            }

            ThreeWayPartition.Bounds bounds = ThreeWayPartition.partition(
                    values, left, right, metrics, random
            );

            if (k < bounds.equalStart()) {
                right = bounds.equalStart() - 1;
            } else if (k > bounds.equalEnd()) {
                left = bounds.equalEnd() + 1;
            } else {
                metrics.stopTimer();
                return values[k];
            }
        }

        metrics.stopTimer();
        throw new IllegalStateException("Selection failed");
    }
}
