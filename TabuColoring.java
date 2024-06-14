//package ynu.ls.coloring;

import java.util.Random;

public class TabuColoring {
    // 类变量声明
    static int N, K; // N: 图的顶点数, K: 可用颜色数
    static int[] sol; // 每个顶点的当前颜色
    static int[][] adjColorTable; // 邻域颜色表，记录每个顶点邻接顶点的颜色分布
    static int[][] tabuTenure; // 禁忌期表，记录变更颜色的禁忌期
    static int f, bestF; // f: 当前冲突数, bestF: 历史最小冲突数
    static int[][] NbID; // 每个顶点的邻接顶点索引数组
    static int[] numAdj; // 每个顶点的邻接顶点数量
    static boolean[][] Adj; // 邻接矩阵，如果两个顶点相邻则为true
    static int[][] equDelt; // 保存相等解的数组
    static Random rand = new Random(); // 全局随机数生成器
    static int delt; // 此次迭代的最优解
    static int equCount = 0; // 记录相等解的个数
    static int iter; // 迭代次数
    static int selVertex, selColor; // move
    static boolean solved = false;
    static int[] storedSolution;
    private final Graph graph; // 图对象
    private final int numColors; // 使用的颜色数量

    // 构造函数
    public TabuColoring(Graph graph, int numColors) {
        this.graph = graph;
        this.numColors = numColors;
        this.initialize();
    }

    // 寻找最佳移动
    public static void findMove() {
        delt = Integer.MAX_VALUE;
        for (int i = 0; i < N; i++) {
            int cColor = sol[i]; // 当前顶点的颜色
            int[] hColor = adjColorTable[i]; // 当前顶点的颜色冲突表
            int cColorTable = hColor[cColor]; // 当前顶点的颜色冲突数
            if (cColorTable > 0) { // 如果当前顶点有冲突
                int[] hTabu = tabuTenure[i];
                for (int j = 0; j < K; j++) {
                    if (cColor != j) { // 如果当前颜色不等于新颜色
                        int tmpDelt = hColor[j] - cColorTable;
                        if (tmpDelt <= delt && (iter > hTabu[j] || (tmpDelt + f) < bestF)) {
                            if (tmpDelt < delt) { // 当前解小于本次迭代最优解,则重新开始保存解
                                equCount = 0;
                                delt = tmpDelt;
                            }
                            equDelt[equCount][0] = i; // 颜色更改的顶点是第 i 个顶点
                            equDelt[equCount][1] = j; // 顶点 i 将尝试更改到的新颜色是索引 j 的颜色
                            equCount++; // 为下一个可能的移动提供空间
                        }
                    }
                }
            }
        }

        int tmp = rand.nextInt(equCount);  // 有多个解时，随机选择
        selVertex = equDelt[tmp][0];
        selColor = equDelt[tmp][1];
    }

    // 执行移动，更新颜色和禁忌表
    public static void makeMove() {
        f = delt + f;
        if (f < bestF) bestF = f;
        int oldColor = sol[selVertex];
        sol[selVertex] = selColor; // 更新顶点颜色
        tabuTenure[selVertex][oldColor] = iter + f + (int) (0.6 * rand.nextInt(10)); // 更新禁忌表
        int[] hNbID = NbID[selVertex]; // 当前顶点的邻接顶点列表
        int numEdge = numAdj[selVertex]; // 当前顶点的邻接顶点数量
        for (int i = 0; i < numEdge; i++) { // 更新邻接顶点的颜色冲突表
            int tmp = hNbID[i]; // 当前邻接顶点
            adjColorTable[tmp][oldColor]--; // 旧颜色冲突数减一
            adjColorTable[tmp][selColor]++; // 新颜色冲突数加一
        }
    }

    // 初始化方法，用于设置初始颜色和计算邻接关系
    public void initialize() {
        N = graph.verNum;
        K = numColors;
        Adj = new boolean[N][N];
        f = 0;
        Random rand = new Random();
        sol = new int[N];
        // 随机为每个顶点分配颜色，并建立邻接矩阵
        for (int i = 0; i < N; i++) {
            sol[i] = rand.nextInt(K);
            for (int j : graph.getNeighbors(i)) {
                Adj[i - 1][j - 1] = true;
                Adj[j - 1][i - 1] = true;
            }
        }
        // 初始化equDelt数组，用于存储等价的最优解
        // 开辟equDelt,最多有N*(K-1)个解.即每个顶点可以产生K-1个delt
        // equDelt[i][0]存第i个解的顶点，equDelt[i][2]存第i个解的newcolor
        equDelt = new int[N * (K - 1)][2];
        adjColorTable = new int[N][K];
        // 初始化颜色冲突表
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                if (Adj[i][j]) { // 如果两个顶点相邻
                    adjColorTable[i][sol[j]]++;
                    adjColorTable[j][sol[i]]++;
                    if (sol[i] == sol[j]) {
                        f++; // 两点颜色相等，则冲突数加一
                    }
                }
            }
        }
        bestF = f;  // 初始化最优f

        tabuTenure = new int[N][K];
        numAdj = new int[N];
        NbID = new int[N][N - 1];
        for (int i = 0; i < N; i++) {   // 从邻接矩阵 Adj 提取每个顶点的邻接顶点列表
            int count = 0;
            for (int j = 0; j < N; j++) {
                if (Adj[i][j]) {
                    NbID[i][count++] = j;
                }
            }
            numAdj[i] = count;
        }
        solved = false;
    }

    // 搜索方法，执行直到找到无冲突解
    public int[] search() {
        if (solved) {
            return storedSolution;
        }
        this.initialize();
        while (f != 0) {
            findMove();
            makeMove();
            iter++;
        }
        // 将解中的每个颜色编号增加1，并在数组前加0
        int[] original = sol;
        sol = new int[original.length + 1]; // 创建一个新的数组，长度比原数组多1
        sol[0] = 0; // 在新数组的最前面加上0
        for (int i = 0; i < original.length; i++) {
            sol[i + 1] = original[i] + 1;
        }
        solved = true;
        storedSolution = sol.clone();
        return sol;
    }
}
