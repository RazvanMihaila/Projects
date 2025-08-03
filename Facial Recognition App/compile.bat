@echo off
echo === Compilare proiect ===

set CLASSPATH=.;opencv-4110.jar;flatlaf-3.6.jar

set SRC=src
set BIN=bin

if not exist %BIN% mkdir %BIN%

javac -cp "%CLASSPATH%" -d %BIN% %SRC%\svm\*.java %SRC%\hog\*.java %SRC%\face\*.java %SRC%\camera\*.java %SRC%\utils\*.java %SRC%\ui\*.java

echo === Compilare finalizata ===
