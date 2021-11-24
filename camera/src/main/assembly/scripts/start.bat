@echo off
set ti=${artifactId}
title %ti%
java -jar ../lib/${artifactId}-${version}.jar
pause