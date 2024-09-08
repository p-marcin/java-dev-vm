:: RESTARTS CONTAINER
@echo off
setlocal
set imageVersion=${project.version}-community
set imageName=ubuntu-dev-vm

echo Stopping %imageName%...
docker container stop %imageName% > nul 2>&1

echo Removing %imageName%...
docker container rm %imageName% > nul 2>&1

echo Starting %imageName%:%imageVersion%...
docker container run --privileged --gpus all -d --restart unless-stopped ^
    --name %imageName% ^
    --hostname %imageName% ^
    -p 80:80 -p 443:443 ^
    --mount source=projects,target=/home/dev/projects ^
    --mount source=maven,target=/home/dev/.m2/repository ^
    --mount source=home,target=/home/dev ^
    --mount source=docker,target=/var/lib/docker ^
    --mount type=bind,source=%USERPROFILE%/shared,target=/mnt/shared ^
    --shm-size 2g ^
    javowiec/%imageName%:%imageVersion% > nul

echo Cleaning dangling images...
docker image prune -f > nul

echo DONE!
pause
endlocal
