@echo off
set ROOT_DIR=%~dp0
set LIB_DIR=%ROOT_DIR%\lib
set WORK_DIR=%ROOT_DIR%\work\%1

TITLE %1

cd %WORK_DIR%

java %JAVA_OPTS% -jar "%LIB_DIR%\org.eclipse.osgi-${version.equinox}.jar" -clean -console -configuration "%WORK_DIR%\conf" -data "%WORK_DIR%\data"