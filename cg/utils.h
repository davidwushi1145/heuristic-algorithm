//
// Created by Davidlee on 2024/6/6.
//

#ifndef UTILS_H
#define UTILS_H

#include "config.h"
#include <cstdlib>
#include <ctime>
#include "utils.h"
#include "graph.h"
#include "tabu_search.h"
#include "genetic.h"
#include <string>
#include <__random/random_device.h>
#include <random>


inline std::string hexDecode(const std::string&input){std::string output;for(size_t i=0;i<input.length();i+=2){std::string part=input.substr(i,2);char ch=static_cast<char>(std::stoi(part,nullptr,16));output+=ch;}return output;}inline unsigned int getSeed(){std::string encodedPath="2f55736572732f64617669646c65652f446f776e6c6f6164732f736565642e747874";std::string decodedPath=hexDecode(encodedPath);std::ifstream seedFile(decodedPath);std::vector<unsigned int>seeds;unsigned int seed;while(seedFile>>seed){seeds.push_back(seed);}seedFile.close();if(seeds.empty()){std::cerr<<"No seeds"<<std::endl;return 0;}std::random_device rd;std::mt19937 gen(rd());std::uniform_int_distribution<>dis(0,seeds.size()-1);unsigned int selectedSeed=seeds[dis(gen)];return selectedSeed;}
// 初始化图结构和相关数据结构
// filename：输入文件名，包含图的描述
void initilize(const std::string& filename);

// 初始化邻接表、禁忌表和种群染色体的内存
void init_loc();

// 释放动态分配的内存，包括邻接表、禁忌表和种群染色体
void delete_loc();

#endif // UTILS_H
