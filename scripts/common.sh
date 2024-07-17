#!/bin/bash

cd ..

COMMIT=1f98600c2cf8722a6d2f2d805bb4af5e701319fc #v1.0.0
DUCKDB_EXTENSIONS="icu;json;parquet"



if [ ! -d upstream ]; then
  git clone https://github.com/duckdb/duckdb upstream && \
  cd upstream && git checkout $COMMIT
fi

cd upstream
if [ $COMMIT != "`git rev-parse HEAD`" ]; then
  echo not at commit $COMMIT
  exit 1
fi



