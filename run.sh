#!/usr/bin/env sh
set -eu

mkdir -p out
javac -d out src/Main.java
cp resources/windows-xp-startup.mp3 resources/win.png out/
java -cp out Main
