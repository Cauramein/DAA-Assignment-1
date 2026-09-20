package task;

public class AlgorithmMetrics {
    private long comparisons;
    private int maxDepth;
    private long startedAt;
    private long elapsedNanos;

    public void reset() {
        comparisons = 0;
        maxDepth = 0;
        elapsedNanos = 0;
    }

    public void startTimer() {
        startedAt = System.nanoTime();
    }

    public void stopTimer() {
        elapsedNanos = System.nanoTime() - startedAt;
    }

    public void addComparison() {
        comparisons++;
    }

    public void visitDepth(int depth) {
        maxDepth = Math.max(maxDepth, depth);
    }

    public long comparisons() {
        return comparisons;
    }

    public int maxDepth() {
        return maxDepth;
    }

    public double timeMs() {
        return elapsedNanos / 1_000_000.0;
    }
}
