cmake_minimum_required(VERSION 3.22.1)
project(forma_hello)

add_library(forma_hello SHARED hello.cpp)

find_library(log-lib log)
target_link_libraries(forma_hello ${log-lib})
