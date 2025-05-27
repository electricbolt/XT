@echo off

rem XT launcher batch file.

where java >NUL 2>NUL
if %ERRORLEVEL%==1 (
    @echo Java not found. Install Java 17 or greater JRE and try again.
	goto :EOF
)

java Version
if %ERRORLEVEL%==1 (
    @echo Java not compatible. Install Java 17 or greater JRE and try again.
)

java -jar xt.jar %*
