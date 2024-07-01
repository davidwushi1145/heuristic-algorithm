//
// Created by Davidlee on 2024/6/6.
//

#include "tabu_search.h"
#include "config.h"
#include "graph.h"

// 全局变量声明
int **adj_table; // 邻接表
int **tt; // 禁忌表
int best_f; // 最佳适应度
int delt; // 当前迭代的delta值
int iter; // 当前迭代次数
int equ_count; // 相等的解数量
int equ_delt[2000][2]; // 保存相等解的数组
int sel_vertex; // 选择的顶点
int sel_color; // 选择的颜色

// 初始化邻接表
void init_adj(int *p) {
    for (int i = 0; i < N; i++)
        memset(adj_table[i], 0, K * sizeof(int)); // 初始化邻接表为0

    int c_color;
    int e_v, e_color;

    // 填充邻接表
    for (int i = 0; i < N; i++) {
        c_color = p[i];
        for (int j = 0; j < num_adj[i]; j++) {
            e_v = nb_v[i][j];
            e_color = p[e_v];
            adj_table[i][e_color]++; // 记录邻接顶点的颜色
        }
    }
}

// 初始化禁忌表
void init_tt() {
    for (int i = 0; i < N; i++)
        memset(tt[i], 0, K * sizeof(int)); // 初始化禁忌表为0
}

// 查找移动
void findmove(int *p, int &f) {
    delt = 10000; // 初始化delta为最大值
    int tmp_delt;
    int c_color;
    int *h_color;
    int *h_tabu;
    int c_color_table;

    // 遍历所有顶点
    for (int i = 0; i < N; i++) {
        c_color = p[i];
        h_color = adj_table[i];
        c_color_table = h_color[c_color];
        if (c_color_table > 0) { // 如果当前顶点有冲突
            h_tabu = tt[i];
            for (int j = 0; j < K; j++) {
                if (c_color != j) { // 尝试所有不同的颜色
                    tmp_delt = h_color[j] - c_color_table;
                    if (tmp_delt <= delt && (iter > h_tabu[j] || (tmp_delt + f) < best_f)) {
                        if (tmp_delt < delt) {
                            equ_count = 0;
                            delt = tmp_delt;
                        }
                        equ_delt[equ_count][0] = i;
                        equ_delt[equ_count][1] = j;
                        equ_count++;
                    }
                }
            }
        }
    }

    // 随机选择一个相等解
    int tmp = rand() % equ_count;
    sel_vertex = equ_delt[tmp][0];
    sel_color = equ_delt[tmp][1];
}

// 执行移动
void makemove(int *p, int &f) {
    f = delt + f; // 更新适应度
    if (f < best_f) best_f = f; // 更新最佳适应度
    int old_color = p[sel_vertex];
    p[sel_vertex] = sel_color; // 更新顶点颜色
    tt[sel_vertex][old_color] = iter + 0.6 * f + (rand() % 10); // 更新禁忌表
    int *h_NbID = nb_v[sel_vertex];
    int num_edge = num_adj[sel_vertex];
    int tmp;
    for (int i = 0; i < num_edge; i++) {
        tmp = h_NbID[i];
        adj_table[tmp][old_color]--;
        adj_table[tmp][sel_color]++; // 更新邻接表
    }
}

// Tabu搜索算法
void Tabu(int *p, int &f) {
    best_f = f; // 初始化最佳适应度
    init_adj(p); // 初始化邻接表
    init_tt(); // 初始化禁忌表
    iter = 0; // 初始化迭代次数

    // 进行Tabu搜索
    while (iter < MAX_TABU_ITER && best_f) {
        findmove(p, f); // 查找移动
        makemove(p, f); // 执行移动
        iter++; // 增加迭代次数
    }
}
