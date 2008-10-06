echo Hello World!
set SLC_HOME=%~dp0..
echo SLC_HOME=%SLC_HOME%

set CLASSPATH=

FOR %%f IN (%SLC_HOME%\lib\te*.jar) DO set CLASSPATH=%CLASSPATH%;%%f

echo CLASSPATH=%CLASSPATH%