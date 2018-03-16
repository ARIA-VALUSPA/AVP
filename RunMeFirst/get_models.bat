@ECHO OFF

FOR /F "tokens=1,2" %%i in (model_all.lst) DO (
	IF EXIST ../Agent-Input/%%i (
		ECHO found %%i
	) ELSE (
		set FILE=%%i
		set URL=%%j
		ECHO Downloading %%i
		curl.exe -L -o ../Agent-Input/%%i %%j
	)
)
