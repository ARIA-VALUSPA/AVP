@echo off
setlocal

if not exist "%~dpn0.sh" echo Script "%~dpn0.sh" not found & exit 2

set _CYGBIN=C:\cygwin\bin
if not exist "%_CYGBIN%" echo Couldn't find Cygwin at "%_CYGBIN%" & exit 3

:: Resolve ___.sh to /cygdrive based *nix path and store in %_CYGSCRIPT%
for /f "delims=" %%A in ('%_CYGBIN%\cygpath.exe "%~dpn0.sh"') do set _CYGSCRIPT=%%A
for /f "delims=" %%A in ('%_CYGBIN%\cygpath.exe "%CD%"') do set _CYGPATH=%%A

:: Throw away temporary env vars and invoke script, passing any args that were passed to us
cd %CD%
endlocal & %_CYGBIN%\bash --login "%_CYGSCRIPT%" %*