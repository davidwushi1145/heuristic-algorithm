//
// Created by Davidlee on 2024/6/6.
//

#ifndef TABU_SEARCH_H
#define TABU_SEARCH_H

#include <cstring>
#include <cstdlib>
#include "config.h"

// 全局变量声明
extern int **adj_table; // 邻接表
extern int **tt; // 禁忌表
extern int best_f; // 最佳适应度
extern int delt; // 当前迭代的delta值
extern int iter; // 当前迭代次数
extern int equ_count; // 相等的解数量
extern int equ_delt[2000][2]; // 保存相等解的数组
extern int sel_vertex; // 选择的顶点
extern int sel_color; // 选择的颜色

// 函数声明

// 初始化邻接表
void init_adj(int *p);

// 初始化禁忌表
void init_tt();

// 查找移动
void findmove(int *p, int &f);

// 执行移动
void makemove(int *p, int &f);

// Tabu搜索算法
void Tabu(int *p, int &f);

#endif // TABU_SEARCH_H
