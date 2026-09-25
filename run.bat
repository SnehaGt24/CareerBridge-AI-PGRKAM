@echo off
cd /d %~dp0
if exist out rmdir /s /q out
mkdir out
for /r src %%f in (*.java) do call set FILES=%%FILES%% "%%f"
javac --add-modules jdk.httpserver -d out %FILES%
if errorlevel 1 pause & exit /b 1
java --add-modules jdk.httpserver -cp out com.prj301.pgrkam.Main
pause
