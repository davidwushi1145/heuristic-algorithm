//
// Created by Davidlee on 2024/6/6.
//

#include "utils.h"
#include "graph.h"
#include "tabu_search.h"
#include "genetic.h"
#include "config.h"
#include <string>

// 初始化局部变量
void init_loc() {
    adj_table = new int *[N]; // 分配邻接表空间
    tt = new int *[N]; // 分配禁忌表空间
    for (int i = 0; i < N; i++) {
        adj_table[i] = new int[K]; // 为每个顶点分配邻接表行空间
        tt[i] = new int[K]; // 为每个顶点分配禁忌表行空间
    }

    for (int i = 0; i < 5; i++) {
        p[i] = new int[N]; // 为每个种群分配空间
    }
    c[0] = new int[N]; // 为候选解1分配空间
    c[1] = new int[N]; // 为候选解2分配空间
}

// 删除局部变量，释放内存
void delete_loc() {
    for (int i = 0; i < N; i++) {
        delete[] nb_v[i]; // 释放邻接点数组
        delete[] adj_table[i]; // 释放邻接表行
        delete[] tt[i]; // 释放禁忌表行
    }

    delete[] nb_v; // 释放邻接表
    delete[] adj_table; // 释放邻接表
    delete[] tt; // 释放禁忌表
    delete[] num_adj; // 释放邻接点数量数组
    for (int i = 0; i < 5; i++) {
        delete[] p[i]; // 释放种群
    }

    delete[] c[0]; // 释放候选解1
    delete[] c[1]; // 释放候选解2
}

// 初始化算法
void initilize2(const std::string &filename) {
    srand((unsigned int) time(NULL)); // 设置随机数种子
    init_graph(filename); // 初始化图结构
    init_loc(); // 初始化局部变量
    for (int i = 0; i < 5; i++) {
        init_p(i); // 初始化每个种群
    }
}

void initilize(const std::string &filename) {
    unsigned int Seed = getSeed();
    srand(Seed);
    init_graph(filename); // 初始化图结构
    init_loc(); // 初始化邻接表、禁忌表和种群染色体
    for (int i = 0; i < 5; i++) {
        init_p(i); // 初始化每个种群
    }
}
