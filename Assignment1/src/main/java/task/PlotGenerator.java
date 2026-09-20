package task;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class PlotGenerator {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 700;
    private static final int LEFT = 90;
    private static final int RIGHT = 260;
    private static final int TOP = 70;
    private static final int BOTTOM = 80;

    public static void main(String[] args) throws IOException {
        List<Row> rows = readCsv("results.csv");
        createPlot(rows, "time", "Time vs n", "Time (ms)", "time_vs_n.png");
        createPlot(rows, "depth", "Max recursion depth vs n", "Max depth", "depth_vs_n.png");
        createPlot(rows, "ratio", "Ratio vs n", "Normalized comparisons", "ratio_vs_n.png");
        System.out.println("Created time_vs_n.png, depth_vs_n.png, ratio_vs_n.png");
    }

    private static List<Row> readCsv(String file) throws IOException {
        List<Row> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",");
                rows.add(new Row(
                        p[0], p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3]),
                        Long.parseLong(p[4]), Integer.parseInt(p[5])
                ));
            }
        }

        return rows;
    }

    private static void createPlot(List<Row> rows, String mode, String title,
                                   String yLabel, String fileName) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        int chartWidth = WIDTH - LEFT - RIGHT;
        int chartHeight = HEIGHT - TOP - BOTTOM;

        double maxValue = 1.0;
        for (Row row : rows) {
            maxValue = Math.max(maxValue, value(row, mode));
        }

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(LEFT, TOP, LEFT, TOP + chartHeight);
        g.drawLine(LEFT, TOP + chartHeight, LEFT + chartWidth, TOP + chartHeight);

        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString(title, LEFT, 35);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("n", LEFT + chartWidth / 2, HEIGHT - 20);
        g.drawString(yLabel, 10, TOP - 15);

        int[] sizes = {1_000, 10_000, 100_000, 1_000_000};
        for (int i = 0; i < sizes.length; i++) {
            int x = LEFT + i * chartWidth / 3;
            g.setColor(new Color(225, 225, 225));
            g.drawLine(x, TOP, x, TOP + chartHeight);
            g.setColor(Color.BLACK);
            g.drawString(formatN(sizes[i]), x - 18, TOP + chartHeight + 25);
        }

        for (int i = 0; i <= 5; i++) {
            int y = TOP + chartHeight - i * chartHeight / 5;
            double label = maxValue * i / 5.0;
            g.setColor(new Color(225, 225, 225));
            g.drawLine(LEFT, y, LEFT + chartWidth, y);
            g.setColor(Color.BLACK);
            g.drawString(String.format(Locale.US, "%.2f", label), 25, y + 5);
        }

        Map<String, List<Row>> groups = groupRows(rows);
        int index = 0;

        for (Map.Entry<String, List<Row>> entry : groups.entrySet()) {
            Color color = Color.getHSBColor(index / (float) groups.size(), 0.75f, 0.80f);
            g.setColor(color);
            g.setStroke(new BasicStroke(2f));

            List<Row> group = entry.getValue();
            group.sort(Comparator.comparingInt(Row::n));

            for (int i = 0; i < group.size() - 1; i++) {
                Row a = group.get(i);
                Row b = group.get(i + 1);
                g.drawLine(xForN(a.n(), chartWidth), yFor(value(a, mode), maxValue, chartHeight),
                        xForN(b.n(), chartWidth), yFor(value(b, mode), maxValue, chartHeight));
            }

            for (Row row : group) {
                int x = xForN(row.n(), chartWidth);
                int y = yFor(value(row, mode), maxValue, chartHeight);
                g.fillOval(x - 4, y - 4, 8, 8);
            }

            int legendY = TOP + index * 22;
            g.fillRect(WIDTH - RIGHT + 20, legendY - 8, 18, 4);
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey(), WIDTH - RIGHT + 45, legendY);
            index++;
        }

        g.dispose();
        ImageIO.write(image, "png", new File(fileName));
    }

    private static Map<String, List<Row>> groupRows(List<Row> rows) {
        Map<String, List<Row>> groups = new LinkedHashMap<>();
        for (Row row : rows) {
            String key = row.algorithm() + "_" + row.input();
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        return groups;
    }

    private static double value(Row row, String mode) {
        return switch (mode) {
            case "time" -> row.timeMs();
            case "depth" -> row.maxDepth();
            case "ratio" -> row.algorithm().equals("quickselect")
                    ? row.comparisons() / (double) row.n()
                    : row.comparisons() / (row.n() * (Math.log(row.n()) / Math.log(2)));
            default -> throw new IllegalArgumentException("Unknown mode");
        };
    }

    private static int xForN(int n, int chartWidth) {
        int index = switch (n) {
            case 1_000 -> 0;
            case 10_000 -> 1;
            case 100_000 -> 2;
            default -> 3;
        };
        return LEFT + index * chartWidth / 3;
    }

    private static int yFor(double value, double maxValue, int chartHeight) {
        return TOP + chartHeight - (int) ((value / maxValue) * chartHeight);
    }

    private static String formatN(int n) {
        return switch (n) {
            case 1_000 -> "1K";
            case 10_000 -> "10K";
            case 100_000 -> "100K";
            default -> "1M";
        };
    }

    private record Row(String algorithm, String input, int n,
                       double timeMs, long comparisons, int maxDepth) {}
}
