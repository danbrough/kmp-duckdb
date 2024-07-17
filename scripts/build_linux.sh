#!/bin/bash

cd "$(dirname "$0")"

source common.sh

#DISABLE_PARQUET=1
#DUCKDB_EXTENSIONS="icu;parquet;json;jemalloc"
#-DENABLE_EXTENSION_AUTOLOADING=1 -DENABLE_EXTENSION_AUTOINSTALL=1 \

KONAN_DIR=$HOME/.konan

#export PATH=$KONAN_DIR/dependencies/llvm-11.1.0-linux-x64-essentials/bin:$PATH
export GEN=ninja
export LLVM_DIR=$KONAN_DIR/dependencies/llvm-11.1.0-linux-x64-essentials/bin
echo clang is `which clang`

PLATFORM_NAME="linux_amd64"
DUCKDB_EXTENSIONS="icu;json;parquet"


#BUILDDIR=./build/$PLATFORM_NAME
#mkdir -p $BUILDDIR && \
#cd $BUILDDIR && \
#cmake -G "Ninja" -DEXTENSION_STATIC_BUILD=1 \
#-DDUCKDB_EXTRA_LINK_FLAGS="-llog" \
#-DBUILD_EXTENSIONS=$DUCKDB_EXTENSIONS \
#-DCMAKE_VERBOSE_MAKEFILE=on \
#-DLOCAL_EXTENSION_REPO=""  -DOVERRIDE_GIT_DESCRIBE="" \
#-DDUCKDB_EXPLICIT_PLATFORM=$PLATFORM_NAME -DBUILD_UNITTESTS=0 -DBUILD_SHELL=1 \
#-DCMAKE_BUILD_TYPE=Release ../..
#cmake --build . --config Release

#JNI_LIBDIR=../../../duckdb/src/androidMain/jniLibs/$ANDROID_ABI
#mkdir -p $JNI_LIBDIR
#cp src/libduckdb.so $JNI_LIBDIR

#$ANDROID_NDK/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-strip $JNI_LIBDIR/libduckdb.so
cd ../upstream
BUILDDIR=./build/$PLATFORM_NAME
mkdir -p $BUILDDIR
cd $BUILDDIR

cmake -G "Ninja" -DFORCE_COLORED_OUTPUT=1  \
    -DEXTENSION_STATIC_BUILD=1 \
   -DCMAKE_RANLIB="/home/dan/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/bin/ranlib" -DCMAKE_AR="$LLVM_DIR/llvm-ar" \
   -DCMAKE_VERBOSE_MAKEFILE=off \
   -DBUILD_EXTENSIONS=$DUCKDB_EXTENSIONS \
   -DDUCKDB_EXPLICIT_PLATFORM=$PLATFORM_NAME -DBUILD_UNITTESTS=0 -DBUILD_SHELL=1 \
   -DCMAKE_CXX_COMPILER="$LLVM_DIR/clang++" -DCMAKE_C_COMPILER="$LLVM_DIR/clang"  -DOVERRIDE_GIT_DESCRIBE="" \
   -DCMAKE_BUILD_TYPE=Release ../..
cmake --build . --config Release
