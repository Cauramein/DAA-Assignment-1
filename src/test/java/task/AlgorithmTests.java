package task;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmTests {
    @Test
    void sortsMatchArraysSortOn100RandomArrays() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {
            int size = random.nextInt(500);
            int[] source = new int[size];
            for (int i = 0; i < size; i++) source[i] = random.nextInt(10_000) - 5_000;

            int[] expected = source.clone();
            Arrays.sort(expected);

            int[] a = source.clone();
            MergeSorter.sort(a, new AlgorithmMetrics());
            assertArrayEquals(expected, a);

            int[] b = source.clone();
            SafeQuickSort.sort(b, new AlgorithmMetrics());
            assertArrayEquals(expected, b);
        }
    }

    @Test
    void edgeCases() {
        int[][] cases = {{}, {1}, {4, 4, 4, 4}, {1, 2, 3, 4, 5}};
        for (int[] source : cases) {
            int[] expected = source.clone();
            Arrays.sort(expected);

            int[] a = source.clone();
            MergeSorter.sort(a, new AlgorithmMetrics());
            assertArrayEquals(expected, a);

            int[] b = source.clone();
            SafeQuickSort.sort(b, new AlgorithmMetrics());
            assertArrayEquals(expected, b);
        }
    }

    @Test
    void quickSortDepthIsBounded() {
        int n = 100_000;
        int[] data = new int[n];
        for (int i = 0; i < n; i++) data[i] = i;

        AlgorithmMetrics metrics = new AlgorithmMetrics();
        SafeQuickSort.sort(data, metrics);

        int limit = (int) Math.ceil(2 * (Math.log(n) / Math.log(2)));
        assertTrue(metrics.maxDepth() <= limit);
    }

    @Test
    void quickSelectMatchesSortedOn100RandomArrays() {
        Random random = new Random(99);

        for (int test = 0; test < 100; test++) {
            int size = 1 + random.nextInt(500);
            int[] data = new int[size];
            for (int i = 0; i < size; i++) data[i] = random.nextInt(10_000) - 5_000;

            int k = random.nextInt(size);
            int[] sorted = data.clone();
            Arrays.sort(sorted);

            int result = QuickSelector.select(data.clone(), k, new AlgorithmMetrics());
            assertEquals(sorted[k], result);
        }
    }

    @Test
    void quickSelectRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> QuickSelector.select(new int[0], 0, new AlgorithmMetrics()));
        assertThrows(IllegalArgumentException.class,
                () -> QuickSelector.select(new int[]{1, 2, 3}, -1, new AlgorithmMetrics()));
        assertThrows(IllegalArgumentException.class,
                () -> QuickSelector.select(new int[]{1, 2, 3}, 3, new AlgorithmMetrics()));
    }
}
