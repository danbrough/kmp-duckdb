#!/bin/bash

cd "$(dirname "$0")"

[ ! -d bin ] && mkdir bin
cd bin

VERSION=v1.0.0


function download_archive() {
    ARCHIVE="$1"
    BINDIR="$2"
    URL="https://github.com/duckdb/duckdb/releases/download/$VERSION/$ARCHIVE"
    ZIP="$BINDIR/$ARCHIVE"
    [ ! -d "$BINDIR" ] && mkdir -p "$BINDIR"
    if [ ! -f "$ZIP" ]; then
        echo downloading $URL to $ZIP
        curl $URL -o "$ZIP" || exit 1
    fi
    cd "$BINDIR" && unzip $ZIP
}

MACHINE="$(uname -s)"
if [ "$MACHINE" == "Linux" ]; then
  download_archive    libduckdb-linux-amd64.zip  amd64
  download_archive    libduckdb-linux-aarch64.zip  aarch64
  download_archive    libduckdb-osx-universal.zip darwin
elif [ "$MACHINE" == "Mac" ]; then
  download_archive libduckdb-osx-universal.zip darwin
fi










