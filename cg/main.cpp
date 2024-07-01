#include "graph.h"
#include "tabu_search.h"
#include "genetic.h"
#include "utils.h"
#include "config.h"
#include <iostream>
#include <omp.h>

int main() {
    int count = 0; // 计数器，用于记录运行次数
    clock_t start, end; // 用于记录算法运行时间的变量
    std::string filename = "/Users/davidlee/Downloads/cg/DSJC500.5.col";
    std::cout << "file:" << filename << "\t k=" << K << "\t L=" << MAX_TABU_ITER << std::endl;
#ifdef _OPENMP
    std::cout << "OpenMP is enabled. Number of threads: " << omp_get_max_threads() << std::endl;
#else
    std::cout << "OpenMP is not enabled." << std::endl;
#endif

    // 循环执行算法20次
    while (count != 20) {
        initilize(filename); // 初始化图和种群
        int generation = 0, cycle = 0; // 记录代数和周期的变量
        start = clock(); // 记录开始时间
        do {
            int f_tmp1, f_tmp2;
            omp_lock_t lock;
            omp_init_lock(&lock);

            #pragma omp parallel sections private(f_tmp1, f_tmp2)
            {
                #pragma omp section
                {
                    GPX(p[P1], p[P2], c[0]); // 进行GPX交叉，生成子代c[0]
                    f_tmp1 = count_f(c[0]); // 计算子代c[0]的冲突数

                    omp_set_lock(&lock);
                    Tabu(c[0], f_tmp1); // 对子代c[0]进行禁忌搜索优化
                    omp_unset_lock(&lock);

                    memcpy(p[0], c[0], N * sizeof(int)); // 更新种群p[0]
                    f[0] = f_tmp1;
                }

                #pragma omp section
                {
                    GPX(p[P2], p[P1], c[1]); // 进行GPX交叉，生成子代c[1]
                    f_tmp2 = count_f(c[1]); // 计算子代c[1]的冲突数

                    omp_set_lock(&lock);
                    Tabu(c[1], f_tmp2); // 对子代c[1]进行禁忌搜索优化
                    omp_unset_lock(&lock);

                    memcpy(p[1], c[1], N * sizeof(int)); // 更新种群p[1]
                    f[1] = f_tmp2;
                }
            }

            omp_destroy_lock(&lock);


            // 选择当前最优种群
            int min_f = 10000;
            for (int i = 0; i < 3; i++) {
                if (min_f > f[i]) {
                    min_f = f[i];
                    best_f = i;
                }
            }
            copy_popu(E1, best_f); // 将最优种群复制到E1

            // 更新最佳个体
            if (f[BEST] > f[E1]) copy_popu(BEST, E1);

            // 每经过一个周期进行种群更新
            if (generation % ITER_CYCLE == 0) {
                copy_popu(P1, E2);
                copy_popu(E2, E1);
                init_p(E1);
                cycle++;
            }
            generation++;
        } while (f[BEST] && !dH_equ_check() && generation < MAX_CROSS); // 检查终止条件

        end = clock(); // 记录结束时间
        double duration = double(end - start) / CLOCKS_PER_SEC; // 转换为秒
        std::cout << "NO." << count + 1 << "\t cons = " << f[BEST] << "\t generation = " << generation << "\t time = "
                  << duration << " 秒"
                  << std::endl;

        count++;

        // 打印每个节点的最终颜色
//        std::cout << "Final colors of nodes:" << std::endl;
//        for (int i = 1; i <= N; i++) {
//            std::cout << "Node " << i << ": " << p[BEST][i] + 1 << std::endl; // 输出颜色，从1开始
//        }
    }

    delete_loc(); // 释放动态分配的内存

    return 0;
}
