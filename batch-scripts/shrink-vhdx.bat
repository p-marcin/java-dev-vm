:: WARNING: SHUT DOWN DOCKER DESKTOP BEFORE RUNNING THIS SCRIPT
:: SHRINKS DOCKER DESKTOP VHDX DISK WHICH GROWS OVER TIME
:: YOU CAN CHECK THE SIZE UNDER PATH: %LOCALAPPDATA%\Docker\wsl\disk
@echo off
setlocal
set vhdxFile=docker_data.vhdx
set vhdxPath=Docker\wsl\disk

echo Shutting down WSL...
wsl --shutdown

echo Creating a temporary file with diskpart commands...
set diskpartCommands=%TEMP%\diskpart_commands.txt
echo select vdisk file="%%LOCALAPPDATA%%\%vhdxPath%\%vhdxFile%" > %diskpartCommands%
echo compact vdisk >> %diskpartCommands%
echo exit >> %diskpartCommands%

echo Shrinking %vhdxFile% with diskpart...
diskpart /s %diskpartCommands%

echo Deleting a temporary file with diskpart commands...
del %diskpartCommands%

echo DONE!
pause
endlocal
