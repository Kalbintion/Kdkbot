@ECHO OFF
REM Starting values
SET startid=1
SET endid=999999
SET no=1
REM Update them via line
IF "%1" NEQ "" SET no=%1
IF "%2" NEQ "" SET startid=%2
IF "%3" NEQ "" SET endid=%3

REM Daemon Data
SET /A amtper=(%endid%-%startid%+1)/%no%
SET csid=0
SET cseid=0
SET spawnno=0

REM Display data to user
ECHO Creating %no% instances of Get_GameIDS.bat...
ECHO Starting ID: %startid%
ECHO Ending ID: %endid%
ECHO Amount Per: %amtper%

REM Instance spawner
:SPAWNER
SET /A spawnno=%spawnno%+1
SET /A csid=%startid%+(%amtper%*(%spawnno%-1))
SET /A cseid=%csid%+%amtper%

IF "%spawnno%"=="%no%" IF "%cseid%" NEQ "%endid%" SET cseid=%endid%

ECHO Creating thread #%spawnno%, %csid% to %cseid%
start cmd /c Get_GameIDS %csid% %cseid%

IF %spawnno% NEQ %no% GOTO SPAWNER

REM Sign-off
ECHO.
ECHO Thread creation complete.