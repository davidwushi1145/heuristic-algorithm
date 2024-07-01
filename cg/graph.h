//
// Created by Davidlee on 2024/6/6.
//

#ifndef GRAPH_H
#define GRAPH_H

#include <iostream>
#include <fstream>
#include <vector>
#include <set>
#include <cstring>
#include <ctime>
#include "config.h"

// 全局变量声明
extern int N; // 顶点数
extern int *num_adj; // 每个顶点的邻接点数量
extern int **nb_v; // 邻接表，存储每个顶点的邻接点

// 初始化图结构
void init_graph(const std::string &fileName);

// 删除图结构，释放内存
void delete_graph();

#endif // GRAPH_H
