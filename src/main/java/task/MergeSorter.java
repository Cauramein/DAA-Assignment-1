package task;

public final class MergeSorter {
    private static final int CUTOFF = 15;

    private MergeSorter() {}

    public static void sort(int[] values, AlgorithmMetrics metrics) {
        metrics.reset();
        metrics.startTimer();

        int[] buffer = new int[values.length];

        if (values.length > 1) {
            sortRange(values, buffer, 0, values.length - 1, metrics, 1);
        }

        metrics.stopTimer();
    }

    private static void sortRange(int[] values, int[] buffer, int left, int right,
                                  AlgorithmMetrics metrics, int depth) {
        metrics.visitDepth(depth);

        if (left >= right) {
            return;
        }

        if (right - left + 1 <= CUTOFF) {
            insertionSort(values, left, right, metrics);
            return;
        }

        int middle = left + (right - left) / 2;
        sortRange(values, buffer, left, middle, metrics, depth + 1);
        sortRange(values, buffer, middle + 1, right, metrics, depth + 1);
        merge(values, buffer, left, middle, right, metrics);
    }

    private static void insertionSort(int[] values, int left, int right,
                                      AlgorithmMetrics metrics) {
        for (int i = left + 1; i <= right; i++) {
            int current = values[i];
            int j = i - 1;

            while (j >= left) {
                metrics.addComparison();
                if (values[j] <= current) {
                    break;
                }
                values[j + 1] = values[j];
                j--;
            }

            values[j + 1] = current;
        }
    }

    private static void merge(int[] values, int[] buffer, int left, int middle, int right,
                              AlgorithmMetrics metrics) {
        for (int i = left; i <= right; i++) {
            buffer[i] = values[i];
        }

        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            metrics.addComparison();

            if (buffer[i] <= buffer[j]) {
                values[k++] = buffer[i++];
            } else {
                values[k++] = buffer[j++];
            }
        }

        while (i <= middle) {
            values[k++] = buffer[i++];
        }

        while (j <= right) {
            values[k++] = buffer[j++];
        }
    }
}
