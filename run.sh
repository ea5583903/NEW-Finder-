#!/usr/bin/env sh
set -eu

mkdir -p out
javac -d out src/Main.java
java -cp out Main
