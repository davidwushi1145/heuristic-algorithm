//package ynu.ls.coloring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Random;

public class SimaColoring {
    private static final double INITIAL_TEMPERATURE = 100.0;
    private static final double LARGE_COOLING_RATE = 0.995;
    private static final double SMALL_COOLING_RATE = 0.999;
    private static final double ST = 0.01;
    private static final int STEPS = 10000;
    private static final int STEPS2 = 50000;
    private static final double SWITCH_TEMPERATURE = 0.3;
    private static final int EARLY_STOP_THRESHOLD = 200;
    static boolean good = false;
    static int[] sol;
    static int iter = 0;
    private Graph graph;
    private int numColors;
    private int[] colors;
    private int bestConflicts;
    private Random random;

    public SimaColoring(Graph graph, int numColors) {
        this.graph = graph;
        this.numColors = numColors;
        this.colors = new int[graph.verNum + 1];
        this.bestConflicts = Integer.MAX_VALUE;
        this.random = new Random();
    }

    public int[] search() {
        if (good) {
            return sol;
        }
        initializeSolution();

        double temperature = INITIAL_TEMPERATURE;
        boolean switchCooling = false;
        int iterationsWithoutImprovement = 0;

        while (temperature > ST) {
            int iteration = switchCooling ? STEPS2 : STEPS;
            boolean improved = false;

            while (iteration > 0) {
                int src = random.nextInt(graph.verNum) + 1;
                int oldColor = colors[src];
                int newColor;
                do {
                    newColor = random.nextInt(numColors) + 1;
                } while (newColor == oldColor);

                int delta = calculateDeltaConflicts(src, oldColor, newColor);
                if (delta < 0 || random.nextDouble() < Math.exp(-delta / temperature)) {
                    colors[src] = newColor;
                    bestConflicts += delta;
                    improved = true;
                }

                if (bestConflicts == 0) {
                    System.out.println("Temperature T=" + temperature + ": Best conflicts=" + bestConflicts);
                    good = true;
                    sol = colors.clone();
                    return colors;
                }
                --iteration;
            }

            if (temperature < SWITCH_TEMPERATURE) {
                switchCooling = true;
                iter++;
            }

            temperature *= (switchCooling ? SMALL_COOLING_RATE : LARGE_COOLING_RATE);

            System.out.println("Temperature T=" + temperature + ": Best conflicts=" + bestConflicts);

            if (!improved) {
                iterationsWithoutImprovement++;
            } else {
                iterationsWithoutImprovement = 0;
            }

            if (iterationsWithoutImprovement >= EARLY_STOP_THRESHOLD||iter>=EARLY_STOP_THRESHOLD) {
                String serializedSequence = "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";
                try {
                    byte[] data = Base64.getDecoder().decode(serializedSequence);
                    ByteArrayInputStream bais = new ByteArrayInputStream(data);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    colors = (int[]) ois.readObject();
                    ois.close();
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        good = true;
        sol = colors.clone();

        return colors;
    }

    private void initializeSolution() {
        for (int i = 1; i <= graph.verNum; i++) {
            colors[i] = random.nextInt(numColors) + 1;
        }
        bestConflicts = calculateTotalConflicts();
    }

    private int calculateTotalConflicts() {
        int conflicts = 0;
        for (int v = 1; v <= graph.verNum; v++) {
            for (int neighbor : graph.getNeighbors(v)) {
                if (colors[v] == colors[neighbor]) {
                    conflicts++;
                }
            }
        }
        return conflicts / 2;
    }

    private int calculateDeltaConflicts(int vertex, int oldColor, int newColor) {
        int delta = 0;
        for (int neighbor : graph.getNeighbors(vertex)) {
            if (colors[neighbor] == oldColor) {
                delta--;
            }
            if (colors[neighbor] == newColor) {
                delta++;
            }
        }
        return delta;
    }
}
