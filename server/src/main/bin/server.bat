@echo off
chcp 936
::返回上一層目錄
cd..
::設置JAVA環境路徑
SET JAVA_PATH=%JAVA_HOME%
::設置應用環境路徑
SET SERVER_HOME=%cd%
::設置應用啟動參數
SET STARTUP_MODE=gameserver
::設定應用啟動類
SET SERVER_CLASS="com.tinybee.server.App"
::設置JVM環境參數
SET JAVA_OPTS=-Xms512M -Xmx1024M -Xmn500M -XX:+UseG1GC -XX:MaxGCPauseMillis=200
SET JAVA_OPTS=%JAVA_OPTS% -Dserver.logPath=%SERVER_HOME%
::設置依賴資源
SET CP=%SERVER_HOME%\lib\*;%SERVER_HOME%\config

::顯示訊息
echo %SERVER_HOME% @ %STARTUP_MODE% start

::執行
java %JAVA_OPTS% -cp %CP% %SERVER_CLASS% %STARTUP_MODE%