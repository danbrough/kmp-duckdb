#!/bin/bash

cd "$(dirname "$0")"

[ ! -d bin ] && mkdir bin
cd bin

VERSION=v1.0.0


function download_archive() {
    ARCHIVE="$1"
    FOLDER="$2"
    BINDIR="$(realpath "$FOLDER")"
    [ -d "$BINDIR" ] && echo "$BINDIR already exists" && return
    URL="https://github.com/duckdb/duckdb/releases/download/$VERSION/$ARCHIVE"
    [ ! -f $ARCHIVE ] && ( wget $URL || exit 1 )
    ZIP="$(realpath "$ARCHIVE")"

    [ ! -d "$BINDIR" ] && mkdir -p "$BINDIR"
    cd "$BINDIR" && unzip $ZIP
}

MACHINE="$(uname -s)"
if [ "$MACHINE" == "Linux" ]; then
  download_archive    libduckdb-linux-amd64.zip  amd64
  download_archive    libduckdb-linux-aarch64.zip  aarch64
  download_archive libduckdb-osx-universal.zip darwin
elif [ "$MACHINE" == "Mac" ]; then
  download_archive libduckdb-osx-universal.zip darwin
fi










