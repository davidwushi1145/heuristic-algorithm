//
// Created by Davidlee on 2024/6/6.
//

#include "graph.h"

int N; // 顶点数
int *num_adj; // 每个顶点的邻接点数量
int **nb_v; // 邻接表，存储每个顶点的邻接点

// 初始化图结构
void init_graph(const std::string &fileName) {
    std::ifstream ifs(fileName); // 打开输入文件
    std::string str;
    ifs >> str;
    while (!ifs.eof()) {
        if (str == "edge") {
            ifs >> N;

            num_adj = new int[N]; // 为每个顶点分配空间存储邻接点数量
            memset(num_adj, 0, N * sizeof(int)); // 初始化为0
            nb_v = new int *[N]; // 为邻接表分配空间
            for (int i = 0; i < N; i++) {
                nb_v[i] = new int[N - 1]; // 假设每个顶点最多有N-1个邻接点
                memset(nb_v[i], 0, (N - 1) * sizeof(int)); // 初始化为0
            }
        }
        if (str == "e") {
            int m, n; // 顶点ID
            ifs >> m >> n;
            m--; // 将顶点ID转换为从0开始
            n--;
            nb_v[m][num_adj[m]] = n; // 保存邻接信息
            num_adj[m]++;
            nb_v[n][num_adj[n]] = m;
            num_adj[n]++;
        }
        ifs >> str;
    }
    ifs.close(); // 关闭文件
}

// 删除图结构，释放内存
void delete_graph() {
    for (int i = 0; i < N; i++) {
        delete[] nb_v[i]; // 释放每个顶点的邻接点数组
    }
    delete[] nb_v; // 释放邻接表
    delete[] num_adj; // 释放邻接点数量数组
}
