@echo off

set ROOT=%1

for /D %%i in (%ROOT%\*) do (

	dir /b /a "%%i\*" | >nul findstr "^" && (
		if not exist %%i\project.nova (
			copy log\project.nova %%i\project.nova
		)
	) || (
		rd %%i
	)
	

)