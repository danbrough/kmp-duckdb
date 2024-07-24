#
# Target operating system name.
set(CMAKE_SYSTEM_NAME Linux)
set(CMAKE_SYSTEM_PROCESSOR aarch64)

# Name of C compiler.
set(CMAKE_C_COMPILER "/home/dan/.konan/dependencies/llvm-11.1.0-linux-x64-essentials/bin/clang")
set(CMAKE_CXX_COMPILER "/home/dan/.konan/dependencies/llvm-11.1.0-linux-x64-essentials/bin/clang++")

set(TOOLCHAIN_PREFIX aarch64-unknown-linux-gnu-)

# Where to look for the target environment. (More paths can be added here)
set(CMAKE_FIND_ROOT_PATH /home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2)
set(CMAKE_SYSROOT /home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu)
#CMAKE_<LANG>_COMPILER_TARGET=
set(CMAKE_COMPILER_TARGET aarch64-unknown-linux-gnu)
set(CMAKE_CLANG_COMPILER_TARGET aarch64-unknown-linux-gnu)
set(LLVM_DEFAULT_TARGET_TRIPLE aarch64-unknown-linux-gnu)
//CMAKE_<LANG>_COMPILER_EXTERNAL_TOOLCHAIN
# Adjust the default behavior of the FIND_XXX() commands:
# search programs in the host environment only.
#set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)

# Search headers and libraries in the target environment only.
#set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)
#set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)
#set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE ONLY)

#set(CPACK_DEBIAN_PACKAGE_ARCHITECTURE arm64)
#set(TOOLCHAIN_SYSROOT /home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu)



cmake_minimum_required(VERSION 3.2)

set(CMAKE_CROSSCOMPILING TRUE)
set(CMAKE_SYSTEM_NAME Linux)
set(CMAKE_SYSTEM_PROCESSOR arm)

set(gcc_toolchain "$ENV{USERPROFILE}\\Documents\\Projects\\toolchains\\gcc-mingw32-arm-linux-gnueabihf")
set(triple arm-linux-gnueabihf)
set(cpu_type cortex-a7)
set(fpu_type neon-vfpv4)
set(float_abi_type hard)

set(CMAKE_C_COMPILER clang)
set(CMAKE_C_COMPILER_TARGET ${triple})
set(CMAKE_C_COMPILER_EXTERNAL_TOOLCHAIN ${gcc_toolchain})
set(CMAKE_SYSROOT ${gcc_toolchain}\\arm-linux-gnueabihf\\libc)
set(CMAKE_C_FLAGS_INIT "${CMAKE_C_FLAGS_INIT} -B${gcc_toolchain}\\bin -mcpu=${cpu_type} -mfpu=${fpu_type} -mfloat-abi=${float_abi_type}")

project(arm_test C)

set(arm_test_SRC main.c)

add_executable(arm_test ${arm_test_SRC})
