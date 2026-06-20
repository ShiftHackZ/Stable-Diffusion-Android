cmake_minimum_required(VERSION 3.22.1)

project(sdai_benchmark_runtime LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_library(
    sdai_benchmark
    SHARED
    benchmark_vulkan_probe.cpp
)

target_link_libraries(
    sdai_benchmark
    PRIVATE
    log
    vulkan
)

target_link_options(
    sdai_benchmark
    PRIVATE
    "-Wl,-z,max-page-size=16384"
    "-Wl,-z,common-page-size=16384"
)

target_compile_options(
    sdai_benchmark
    PRIVATE
    -Wall
    -Wextra
    -Wno-unused-parameter
)
