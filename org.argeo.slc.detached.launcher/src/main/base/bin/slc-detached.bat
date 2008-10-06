@echo off
echo SLC Detached
set SLC_HOME=%~dp0..
echo SLC_HOME=%SLC_HOME%
set SLC_LIB_DETACHED=%SLC_HOME%\lib\detached

call slc-detached-settings.bat

rem FOR %%f IN (%SLC_HOME%\lib\detached\*.jar) DO set CLASSPATH=%CLASSPATH%;%%f
FOR %%f IN (%SLC_HOME%\lib\detached\org.argeo.slc.detached.launcher-*.jar) DO set SLC_DETACHED_LAUNCHER_JAR=%%f

set CLASSPATH=%SLC_DETACHED_LAUNCHER_JAR%;%SLC_USER_CLASSPATH%;%SLC_LIB_DETACHED%\aopalliance-1.0.jar;%SLC_LIB_DETACHED%\commons-io-1.4.jar;%SLC_LIB_DETACHED%\commons-logging-1.1.1.jar;%SLC_LIB_DETACHED%\log4j-1.2.15.jar;%SLC_LIB_DETACHED%\org.apache.felix.main-1.2.1.jar;%SLC_LIB_DETACHED%\spring-beans-2.0.8.jar;%SLC_LIB_DETACHED%\spring-context-2.0.8.jar;%SLC_LIB_DETACHED%\spring-core-2.0.8.jar;%SLC_LIB_DETACHED%\xercesImpl-2.8.1.jar;%SLC_LIB_DETACHED%\xml-apis-1.3.03.jar;%SLC_LIB_DETACHED%\xalan-2.7.0.jar

set CMD=%JAVA_HOME%\bin\java %SLC_DETACHED_JVM_ARGS% -Dslc.home=%SLC_HOME% -Dslc.workDir=%SLC_WORK_DIR% "-Dslc.detached.userBundles=%SLC_USER_BUNDLES%" -Dslc.detached.appclass=%SLC_DETACHED_APPCLASS% "-Dslc.detached.appargs=%SLC_DETACHED_APPARGS%" -classpath %CLASSPATH% org.argeo.slc.detached.launcher.Main
rem echo CMD=%CMD%

start %CMD%

