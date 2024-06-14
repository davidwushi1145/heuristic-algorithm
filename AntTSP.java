import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * 在原始蚁群算法上进行的优化
 * 1. 局部搜索优化：
 * - 在每只蚂蚁生成初始路径后，使用 2-opt 算法进行局部搜索优化。
 * - 2-opt 算法通过反转路径中两个节点之间的部分路径来寻找更优解，避免陷入局部最优。
 * 2. 动态终止条件：
 * - 引入了一个动态终止条件：如果当前最佳路径长度与已知最优解（2579）之间的相对误差小于 10%，则提前终止迭代。
 * - 这样可以在接近最优解时节省计算时间。
 * 3. 精英策略增强：
 * - 对全局最优路径进行额外的信息素更新，称为精英策略。
 * - 增加精英蚂蚁的权重（ELITE_WEIGHT），进一步强化全局最优路径的信息素值，促进算法快速收敛。
 * 4. 信息素矩阵重置：
 * - 设置了一个重置阈值（RESET_THRESHOLD），当连续多次迭代没有改进时，重置信息素矩阵。
 * - 通过重置信息素矩阵，避免算法在局部最优解处停滞。
 */

public class AntTSP {
    private static final int MAX_ITERATIONS = 300;
    private static final int NUM_ANTS = 75;
    private static final double ALPHA = 1.0; // 信息素重要性因子
    private static final double BETA = 5.0; // 启发式因子重要性
    private static final double EVAPORATION = 0.3; // 信息素挥发率
    private static final double Q = 100; // 信息素常量
    private static final int RESET_THRESHOLD = 50; // 多少次没有改进后重置信息素矩阵
    private static final double ELITE_WEIGHT = 2.0; // 精英蚂蚁的权重
    private int NB_CITIES;
    private double[][] distMatrix;
    private ArrayList<CityPoint> cityPoints = new ArrayList<>();
    private double[][] pheromoneMatrix;
    private Random random;
    private int noImprovementCount = 0; // 没有改进的计数器
    private int[] bestOverallTour;
    private double bestOverallLength = Double.MAX_VALUE;

    public AntTSP(String filename) {
        buildDistMatrix(filename);
        initializePheromoneMatrix();
        random = new Random();
    }

    public int[] solve() {
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            int[][] allTours = new int[NUM_ANTS][NB_CITIES];
            double[] allLengths = new double[NUM_ANTS];
            int[] bestTour = null;
            double bestLength = Double.MAX_VALUE;

            for (int k = 0; k < NUM_ANTS; k++) {
                allTours[k] = generateTour();
                allTours[k] = localSearch(allTours[k]); // 局部搜索优化路径
                allLengths[k] = getTourLength(allTours[k]);

                if (allLengths[k] < bestLength) {
                    bestLength = allLengths[k];
                    bestTour = allTours[k].clone();
                }
            }

            if (bestLength < bestOverallLength) {
                bestOverallLength = bestLength;
                bestOverallTour = bestTour.clone();
                noImprovementCount = 0;
            } else {
                noImprovementCount++;
            }

            updatePheromones(allTours, allLengths);

            // 打印当前迭代的最佳路径和其长度
//            System.out.print("Iteration " + (iteration + 1) + ": Best Tour Length = " + bestOverallLength + ", Tour: ");
//            for (int city : bestOverallTour) {
//                System.out.print(city + " ");
//            }
//            System.out.println();
            if (((bestOverallLength - 2579) / 2579) < 0.1) {
                break;
            }

            if (noImprovementCount >= RESET_THRESHOLD) {
                initializePheromoneMatrix(); // 重置信息素矩阵
                System.out.println("Pheromone matrix reset due to lack of improvement.");
                noImprovementCount = 0;
            }
        }

        return bestOverallTour;
    }

    private void initializePheromoneMatrix() {
        pheromoneMatrix = new double[NB_CITIES + 1][NB_CITIES + 1];
        for (int i = 1; i <= NB_CITIES; i++) {
            for (int j = 1; j <= NB_CITIES; j++) {
                pheromoneMatrix[i][j] = 1.0;
            }
        }
    }

    private int[] generateTour() {
        int[] tour = new int[NB_CITIES];
        boolean[] visited = new boolean[NB_CITIES + 1];
        int start = random.nextInt(NB_CITIES) + 1;
        tour[0] = start;
        visited[start] = true;

        for (int i = 1; i < NB_CITIES; i++) {
            int nextCity = selectNextCity(tour[i - 1], visited);
            tour[i] = nextCity;
            visited[nextCity] = true;
        }

        return tour;
    }

    private int selectNextCity(int currentCity, boolean[] visited) {
        double[] probabilities = new double[NB_CITIES + 1];
        double sum = 0.0;

        for (int i = 1; i <= NB_CITIES; i++) {
            if (!visited[i]) {
                probabilities[i] = Math.pow(pheromoneMatrix[currentCity][i], ALPHA) *
                        Math.pow(1.0 / distMatrix[currentCity][i], BETA);
                sum += probabilities[i];
            }
        }

        double r = random.nextDouble() * sum;
        double partialSum = 0.0;

        for (int i = 1; i <= NB_CITIES; i++) {
            if (!visited[i]) {
                partialSum += probabilities[i];
                if (partialSum >= r) {
                    return i;
                }
            }
        }

        throw new RuntimeException("No unvisited city found");
    }

    private void updatePheromones(int[][] allTours, double[] allLengths) {
        for (int i = 1; i <= NB_CITIES; i++) {
            for (int j = 1; j <= NB_CITIES; j++) {
                pheromoneMatrix[i][j] *= (1.0 - EVAPORATION);
            }
        }

        for (int k = 0; k < NUM_ANTS; k++) {
            double contribution = Q / allLengths[k];
            for (int i = 0; i < NB_CITIES - 1; i++) {
                int city1 = allTours[k][i];
                int city2 = allTours[k][i + 1];
                pheromoneMatrix[city1][city2] += contribution;
                pheromoneMatrix[city2][city1] += contribution;
            }
            int city1 = allTours[k][NB_CITIES - 1];
            int city2 = allTours[k][0];
            pheromoneMatrix[city1][city2] += contribution;
            pheromoneMatrix[city2][city1] += contribution;
        }

        // 添加精英策略的信息素更新
        double eliteContribution = ELITE_WEIGHT * (Q / bestOverallLength);
        for (int i = 0; i < NB_CITIES - 1; i++) {
            int city1 = bestOverallTour[i];
            int city2 = bestOverallTour[i + 1];
            pheromoneMatrix[city1][city2] += eliteContribution;
            pheromoneMatrix[city2][city1] += eliteContribution;
        }
        int city1 = bestOverallTour[NB_CITIES - 1];
        int city2 = bestOverallTour[0];
        pheromoneMatrix[city1][city2] += eliteContribution;
        pheromoneMatrix[city2][city1] += eliteContribution;
    }

    private double getTourLength(int[] tour) {
        double length = 0.0;
        for (int i = 0; i < NB_CITIES - 1; i++) {
            length += distMatrix[tour[i]][tour[i + 1]];
        }
        length += distMatrix[tour[NB_CITIES - 1]][tour[0]];
        return length;
    }

    private void buildDistMatrix(String filename) {
        Scanner fin;
        try {
            fin = new Scanner(new FileReader(filename));
            while (fin.hasNextLine()) {
                String line = fin.nextLine();
                if (line.startsWith("NODE_COORD_SECTION"))
                    break;
            }
            while (fin.hasNextLine()) {
                String line = fin.nextLine();
                if (line.startsWith("EOF"))
                    break;
                Scanner in = new Scanner(line);
                int no = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                cityPoints.add(new CityPoint(no, x, y));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Failed to open file " + filename);
        }

        if (cityPoints.size() == 0) {
            System.out.println("Read Instance " + filename + " Failed!");
        } else {
            int nb_city = cityPoints.size();
            CityPoint[] cities = new CityPoint[nb_city + 1];
            for (CityPoint p : cityPoints) {
                cities[p.getCityNo()] = p;
            }
            distMatrix = new double[nb_city + 1][nb_city + 1];
            for (int i = 1; i <= nb_city; i++) {
                for (int j = i + 1; j <= nb_city; j++) {
                    int _x = cities[i].getX() - cities[j].getX();
                    int _y = cities[i].getY() - cities[j].getY();
                    distMatrix[i][j] = distMatrix[j][i] = Math.sqrt(_x * _x + _y * _y);
                }
            }
            NB_CITIES = cityPoints.size();
        }
    }

    // 局部搜索算法 2-opt
    private int[] localSearch(int[] tour) {
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            for (int i = 0; i < tour.length - 1; i++) {
                for (int j = i + 1; j < tour.length; j++) {
                    if (j - i == 1) continue; // 相邻节点不需要交换
                    int[] newTour = twoOptSwap(tour, i, j);
                    if (getTourLength(newTour) < getTourLength(tour)) {
                        tour = newTour;
                        improvement = true;
                    }
                }
            }
        }
        return tour;
    }

    private int[] twoOptSwap(int[] tour, int i, int j) {
        int[] newTour = tour.clone();
        while (i < j) {
            int temp = newTour[i];
            newTour[i] = newTour[j];
            newTour[j] = temp;
            i++;
            j--;
        }
        return newTour;
    }
}
