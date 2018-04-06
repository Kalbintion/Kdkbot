@ECHO OFF
MODE con: cols=40 lines=9
SET id=1
SET endid=999999
IF "%1" NEQ "" SET id=%1
IF "%2" NEQ "" SET endid=%2
SET startid=%id%

:MAIN
TITLE Steam App Name Extractor - %id%/%endid% (%startid% - %endid%)
ECHO Retrieving contents for App ID: %id%
REM IF EXIST %id%.txt GOTO SKIP

REM Grab page data, pulling out the title based on tag ID apphub_AppName
CURL -s store.steampowered.com/app/%id%/ |FIND "apphub_AppName" > "%id%.txt"

:SKIP
SET /a id=%id%+1

REM Remove empty files
FOR /R %%F IN (*) DO IF %%~zF==0 DEL "%%F" >nul 2>&1

REM We done with the thing yet? No? Yes?
IF %id% GEQ %endid% GOTO EOF
GOTO MAIN

:EOF