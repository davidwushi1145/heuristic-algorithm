cmake_minimum_required(VERSION 3.28)
project(cg)

set(CMAKE_CXX_STANDARD 17)

# 设置 OpenMP 相关的变量
set(OpenMP_C_FLAGS "-Xpreprocessor -fopenmp")
set(OpenMP_CXX_FLAGS "-Xpreprocessor -fopenmp")
set(OpenMP_C_LIB_NAMES "omp")
set(OpenMP_CXX_LIB_NAMES "omp")
set(OpenMP_omp_LIBRARY "/opt/homebrew/Cellar/libomp/18.1.7/lib/libomp.dylib") # 将该路径替换为自己电脑中libomp.dylib路径

# 设置头文件搜索路径
#include_directories("/opt/homebrew/opt/libomp/include")
include_directories("/opt/homebrew/Cellar/libomp/18.1.7/include")


# 查找 OpenMP 库
find_package(OpenMP REQUIRED)

add_executable(cg main.cpp
        graph.cpp
        tabu_search.cpp
        tabu_search.h
        genetic.cpp
        genetic.h
        utils.cpp
        utils.h
        config.h
        utils.cpp
)

# Link OpenMP
# 如果找到 OpenMP 库，则添加链接
if (OpenMP_CXX_FOUND)
    target_link_libraries(cg PUBLIC OpenMP::OpenMP_CXX)
else()
    message("OpenMP not found")
endif ()
