@echo off
set ROOT_DIR=%~dp0
set WORK_DIR=%ROOT_DIR%\work\%1

TITLE %1

cd %WORK_DIR%

java %JAVA_OPTS% -jar "%ROOT_DIR%\lib\org.eclipse.osgi-${version.equinox}.jar" -clean -console -configuration "%WORK_DIR%\conf" -data "%WORK_DIR%\data"