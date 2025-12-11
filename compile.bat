@echo off
echo Compiling POS Application...

dir /s /B src\*.java > sources.txt
javac -d bin -cp "lib/*" @sources.txt
del sources.txt

echo.
echo Compilation done!
pause