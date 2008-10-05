echo Hello World!
set SLC_HOME=%~dp0..
echo SLC_HOME=%SLC_HOME%

set CLASSPATH=
cd %SLC_HOME%\lib
FOR %f IN (*.jar) DO echo %f

echo CLASSPATH=%CLASSPATH%