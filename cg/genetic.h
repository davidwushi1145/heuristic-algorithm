//
// Created by Davidlee on 2024/6/6.
//

#ifndef GENETIC_H
#define GENETIC_H

#include <set>
#include <vector>
#include <map>
#include <algorithm>
#include <cstring>
#include "config.h"

extern int *p[5]; // 保存五个种群
extern int f[5]; // 保存种群对应的适应度值
extern int *c[2]; // 保存两个候选解

// 函数声明
// 初始化种群
void init_p(int m);

// 计算冲突数
int count_f(int *p);

// 交叉算子
void GPX(int *p1, int *p2, int *c);

// 检查两个解是否相同
bool dH_equ_check();

// 复制种群
void copy_popu(int dst, int src);

#endif // GENETIC_H
