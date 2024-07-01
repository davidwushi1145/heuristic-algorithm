//
// Created by Davidlee on 2024/6/6.
//
#include "genetic.h"
#include "graph.h"
#include "config.h"

// 定义全局变量
int *p[5], f[5]; // 保存五个种群及其适应度值
int *c[2]; // 保存两个候选解

// 初始化种群
void init_p(int m) {
    for (int i = 0; i < N; i++)
        p[m][i] = rand() % K; // 为每个顶点随机分配一个颜色

    f[m] = count_f(p[m]); // 计算种群的冲突数并保存
}

// 计算冲突数
int count_f(int *p) {
    int f = 0;
    int c_color, e_color, e_v;
    for (int i = 0; i < N; i++) {
        c_color = p[i]; // 当前顶点颜色
        for (int j = 0; j < num_adj[i]; j++) {
            e_v = nb_v[i][j]; // 邻接顶点
            e_color = p[e_v]; // 邻接顶点颜色
            if (c_color == e_color) f++; // 如果颜色相同，增加冲突计数
        }
    }
    f = f / 2; // 每个冲突在两个顶点之间计数一次，因此除以2
    return f;
}

// 交叉算子
void GPX(int *p1, int *p2, int *c) {
    std::set<int> p[2][K]; // 保存颜色集合
    int *s[2]; // 保存种群副本
    int n1, n2;
    int color;
    s[0] = new int[N];
    s[1] = new int[N];
    memcpy(s[0], p1, N * sizeof(int));
    memcpy(s[1], p2, N * sizeof(int));

    // 初始化颜色集合
    for (int i = 0; i < N; i++) {
        color = p1[i];
        p[0][color].insert(i);
        color = p2[i];
        p[1][color].insert(i);
    }

    int equ_k[2000];
    int max_size;
    int max_equ_count;
    int tmp;
    for (int l = 0; l < K; l++) {
        n1 = l % 2;
        n2 = (l + 1) % 2;
        max_size = -1;
        for (int i = 0; i < K; i++) {
            tmp = p[n1][i].size();
            if (tmp >= max_size) {
                if (tmp > max_size) {
                    max_size = tmp;
                    max_equ_count = 0;
                }
                equ_k[max_equ_count] = i;
                max_equ_count++;
            }
        }
        int del_color, del_v, del_color_2;
        del_color = equ_k[rand() % max_equ_count]; // 从最大颜色集合中随机选一个颜色
        auto it = p[n1][del_color].begin();
        while (it != p[n1][del_color].end()) {
            del_v = *it;
            del_color_2 = s[n2][del_v];
            p[n2][del_color_2].erase(del_v); // 从另一个种群中移除顶点
            c[del_v] = l; // 分配新颜色
            it++;
        }
        p[n1][del_color].clear();
    }

    // 对剩余顶点随机分配颜色
    for (int i = 0; i < K; i++) {
        auto it = p[0][i].begin();
        while (it != p[0][i].end()) {
            c[*it] = rand() % K;
            it++;
        }
    }
}

// 检查两个解是否相同
bool dH_equ_check() {
    std::set<int> s[2][K];
    int color, size;
    for (int i = 0; i < N; i++) {
        color = p[0][i];
        s[0][color].insert(i);
        color = p[1][i];
        s[1][color].insert(i);
    }
    for (int i = 0; i < K; i++) {
        size = s[0][i].size();
        if (!size) continue;
        auto it = s[0][i].begin();
        for (int j = 0; j < K; j++) {
            if (s[1][j].count(*it)) {
                if (size == s[1][j].size()) {
                    it++;
                    while (it != s[0][i].end()) {
                        if (!s[1][j].count(*it))
                            return false;
                        it++;
                    }
                    s[1][j].clear();
                    break;
                } else {
                    return false;
                }
            }
        }
    }
    return true;
}

// 复制种群
void copy_popu(int dst, int src) {
    memcpy(p[dst], p[src], N * sizeof(int)); // 复制种群
    f[dst] = f[src]; // 复制适应度值
}
