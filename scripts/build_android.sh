#!/bin/bash

cd "$(dirname "$0")" && cd ..


COMMIT=1f98600c2cf8722a6d2f2d805bb4af5e701319fc #v1.0.0

[ -d android-ndk ] && ANDROID_NDK="$PWD/android-ndk"
if [ ! -d upstream ]; then
  git clone https://github.com/duckdb/duckdb upstream && \
  cd upstream && git checkout $COMMIT
fi

cd upstream
if [ $COMMIT != "`git rev-parse HEAD`" ]; then
  echo not at commit $COMMIT
  exit 1
fi


if [ -z "$ANDROID_NDK" ]; then
  echo "\$ANDROID_NDK not set."
  exit 1
fi

#ANDROID_ABI=arm64-v8a 
ANDROID_ABI=x86_64
#ANDROID_ABI=armeabi-v7a

[ ! -z "$1" ] && ANDROID_ABI="$1"


ANDROID_PLATFORM=24
DUCKDB_EXTENSIONS="icu;json;parquet"
#DISABLE_PARQUET=1
#DUCKDB_EXTENSIONS="icu;parquet;json;jemalloc"

PLATFORM_NAME="android_$ANDROID_ABI"
BUILDDIR=./build/$PLATFORM_NAME
mkdir -p $BUILDDIR && \
cd $BUILDDIR && \
cmake -G "Ninja" -DEXTENSION_STATIC_BUILD=1 \
-DDUCKDB_EXTRA_LINK_FLAGS="-llog" \
-DBUILD_EXTENSIONS=$DUCKDB_EXTENSIONS \
-DENABLE_EXTENSION_AUTOLOADING=1 -DENABLE_EXTENSION_AUTOINSTALL=1 \
-DCMAKE_VERBOSE_MAKEFILE=on \
-DANDROID_PLATFORM=$ANDROID_PLATFORM \
-DLOCAL_EXTENSION_REPO=""  -DOVERRIDE_GIT_DESCRIBE="" \
-DDUCKDB_EXPLICIT_PLATFORM=$PLATFORM_NAME -DBUILD_UNITTESTS=0 -DBUILD_SHELL=1 \
-DANDROID_ABI=$ANDROID_ABI -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake \
-DCMAKE_BUILD_TYPE=Release ../.. && \
cmake --build . --config Release
JNI_LIBDIR=../duckdb/src/androidMain/jniLibs/$ANDROID_ABI
mkdir -p $JNI_LIBDIR
cp build/android_$ANDROID_ABI/src/libduckdb.so $JNI_LIBDIR


