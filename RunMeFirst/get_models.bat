for /F "tokens=1,2" %%i in (model_all.lst) do (
	set FILE=%%i
	set URL=%%j
	curl.exe -L -o temp %%j
	move temp "../%%i"
)