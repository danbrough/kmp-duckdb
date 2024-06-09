#!/bin/bash

cd "$(dirname "$0")"

[ ! -d bin ] && mkdir bin
cd bin

VERSION=v1.0.0

function download_archive() {
    ARCHIVE="$1"
    BINDIR="$2"

    URL="https://github.com/duckdb/duckdb/releases/download/$VERSION/$ARCHIVE"

    echo URL $URL ARCHIVE: $ARCHIVE BINDIR:$BINDIR

    [ ! -d "$BINDIR" ] && mkdir -p "$BINDIR"
    cd $BINDIR
    if [ ! -f "$ZIP" ]; then
            echo downloading $URL
            wget $WGETOPTS  $URL || exit 1
    fi
    unzip $ARCHIVE && rm $ARCHIVE
    cd ..
}

MACHINE="$(uname -s)"
if [ "$MACHINE" == "Darwin" ]; then
  # don't know why I need this
  WGETOPTS="--ca-certificate=/etc/ssl/certs/ca-certificates.crt"
fi

download_archive    libduckdb-linux-amd64.zip  amd64
download_archive    libduckdb-linux-aarch64.zip  aarch64
download_archive    libduckdb-osx-universal.zip darwin
download_archive    libduckdb-windows-amd64.zip windows









