#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
rm -rf out && mkdir -p out
javac --add-modules jdk.httpserver -d out $(find src -name '*.java')
java --add-modules jdk.httpserver -cp out com.prj301.pgrkam.Main
