@echo off

rem XT launcher batch file.

rem Directory of where this script is located.
set SCRIPT_DIR=%~dp0

rem Check to see if Java is installed.
where java >NUL 2>NUL
if %ERRORLEVEL%==1 (
    @echo Java not found. Install Java 17 or greater JRE and try again.
    goto :EOF
)

rem Check Java version.
java -cp %SCRIPT_DIR% Version
if %ERRORLEVEL%==1 (
    @echo Java not compatible. Install Java 17 or greater JRE and try again.
    goto :EOF
)

rem Execute XT.
java -jar %SCRIPT_DIR%xt.jar %*
