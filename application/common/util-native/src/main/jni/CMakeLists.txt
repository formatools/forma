cmake_minimum_required(VERSION 3.4.1)

add_library(
    forma-utils
    SHARED
    Secure.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    forma-utils
    ${log-lib}
)
