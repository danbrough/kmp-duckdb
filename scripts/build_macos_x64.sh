#!/bin/bash

cd "$(dirname "$0")"

source common.sh


KONAN_DIR=$HOME/.konan


export GEN=ninja
export LLVM_DIR=$KONAN_DIR/dependencies/apple-llvm-20200714-macos-x64-essentials/bin


PLATFORM_NAME="macos_x64"
DUCKDB_EXTENSIONS="icu;json;parquet"

cd ../upstream
BUILDDIR=./build/$PLATFORM_NAME
mkdir -p $BUILDDIR
cd $BUILDDIR


#CLANG_ARGS="--target=aarch64-unknown-linux-gnu --toolchain=$KONAN_DIR/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2"
export PATH="$LLVM_DIR:$PATH"

echo clang is `which clang` cmake is `which cmake`
#export TOOLCHAIN_PREFIX=aarch64-unknown-linux-gnu-
TARGET=x86_64-apple-darwin
#    KonanTarget.MACOS_X64 -> "x86_64-apple-darwin"
#    KonanTarget.MACOS_ARM64 -> "aarch64-apple-darwin"

cmake -G "Ninja" -DFORCE_COLORED_OUTPUT=1   \
-DCMAKE_SYSTEM_NAME=Darwin \
-DCMAKE_SYSTEM_PROCESSOR=x64 \
  -DCMAKE_CROSSCOMPILING=FALSE \
    -DEXTENSION_STATIC_BUILD=1 \
   -DCMAKE_RANLIB="ranlib"  -DCMAKE_AR="llvm-ar" \
   -DCMAKE_VERBOSE_MAKEFILE=off \
   -DBUILD_EXTENSIONS=$DUCKDB_EXTENSIONS \
   -DDUCKDB_EXPLICIT_PLATFORM=$PLATFORM_NAME -DBUILD_UNITTESTS=0 -DBUILD_SHELL=1 \
   -DCMAKE_CXX_COMPILER="clang++" \
   -DCMAKE_C_COMPILER="clang" \
  -DCMAKE_C_COMPILER_TARGET=$TARGET \
  -DCMAKE_CXX_COMPILER_TARGET=$TARGET
    -DOVERRIDE_GIT_DESCRIBE="" \
   -DCMAKE_BUILD_TYPE=Release ../..

cmake --build . --config Release
#-DCMAKE_FIND_ROOT_PATH=/home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 \
#set(CMAKE_C_COMPILER clang)
##set(CMAKE_C_COMPILER_TARGET ${triple})
#set(CMAKE_C_COMPILER_EXTERNAL_TOOLCHAIN ${gcc_toolchain})
#set(CMAKE_SYSROOT ${gcc_toolchain}\\arm-linux-gnueabihf\\libc)
#-DCMAKE_FIND_ROOT_PATH=/home/dan/.konan/dependencies \
#-DCMAKE_TOOLCHAIN_FILE=../../../scripts/arm64.cmake \
#cmake --build . --config Release
