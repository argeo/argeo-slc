@echo off
set SLC_HOME=%~dp0..

TITLE %1

java %JAVA_OPTS% -jar lib\org.eclipse.osgi-${version.equinox}.jar -clean -console -configuration work\%1\conf -data work\%1\data