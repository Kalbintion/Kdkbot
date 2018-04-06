
REM Extract app name fully from file
IF NOT EXIST %id%.txt GOTO NAMESKIP
ECHO Extracting name for App ID: %id%
SET /P gamename=<"%id%.txt"
REM We may have already extracted name, skip if we did
IF "x%gamename:apphub_=%"=="x%gamename%" GOTO NAMESKIP
SET gamename="%gamename:~30,-6%"
SET gamename=%gamename:~1,-1%
ECHO %gamename%>"%id%.txt"

:NAMESKIP