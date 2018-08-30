#!/bin/bash
#停止應用進程
pid=$(ps -ef | grep gameserver | grep '/bin/java' | grep -v grep | awk '{print $2}')
if [ $pid ]
then
  echo "get pid=$pid and killing"
  if ps -p $pid > /dev/null
  then
    kill -TERM $pid
  fi
fi
#返回上一層目錄
cd /home/pcion/runtime/server-0.0.1-SNAPSHOT
#設置JAVA環境路徑
JAVA_PATH=/usr/bin
#設置應用環境路徑
SERVER_HOME="$(pwd)"
#設置應用啟動參數
STARTUP_MODE="gameserver"
#設定應用啟動類
SERVER_CLASS="com.tinybee.server.App"
#設置JVM環境參數
JAVA_OPTS="-Xms512M -Xmx1024M -Xmn500M -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
JAVA_OPTS="${JAVA_OPTS} -Dserver.logPath=${SERVER_HOME}"
#設置依賴資源
CP=":${SERVER_HOME}/lib/*:${SERVER_HOME}/config"

#顯示訊息
echo "${SERVER_HOME} @ ${STARTUP_MODE} start"

#執行
rm -f in
mkfifo in
nohup $(while true; do cat in; done | ${JAVA_PATH}/java ${JAVA_OPTS} -cp $CP ${SERVER_CLASS} ${STARTUP_MODE} > out) &
#nohup ${JAVA_PATH}/java ${JAVA_OPTS} -cp $CP ${SERVER_CLASS} ${STARTUP_MODE} &