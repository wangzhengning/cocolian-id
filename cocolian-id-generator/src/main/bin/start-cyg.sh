#!/bin/bash
# Run in cygwin
# Author: Shamphone Lee

JAVA="$(cygpath -pm $JAVA_HOME/bin/java)"
WORKING_DIR=$(cd `dirname $0`; pwd)
 
WORKING_DIR="$(dirname ${WORKING_DIR})"

if [ "$(expr substr $(uname -s) 1 9)" == "CYGWIN_NT" ]; then
	echo "Cygwin detected"
#	WORKING_DIR="$(cygpath -m ${WORKING_DIR})"  
fi

echo "running in ${WORKING_DIR}"
 

MAIN_CLASS="org.cocolian.id.server.IdServer"

JAR_LIB="$WORKING_DIR/${project.build.finalName}.jar"
JAR_LIB="${JAR_LIB}:${WORKING_DIR}/lib/*"
JAR_LIB="$(cygpath -pm ${JAR_LIB})"

LOG_PATH="${WORKING_DIR}/logs"
LOG_PATH="$(cygpath -pm ${LOG_PATH})"

# JAVA_JVM_OPTION="-Xmx25g -Xms25g -XX:NewSize=15g -XX:MaxNewSize=15g -XX:SurvivorRatio=17 -XX:PermSize=256m -XX:MaxPermSize=256m"

JAVA_JVM_OPTION="-Xmx1g -Xms1g"
JAVA_JVM_OPTION="${JAVA_JVM_OPTION} -Duser.timezone=GMT+8 -Dcocolian.log.path=${LOG_PATH}"


JAVA_JVM_OPTION="${JAVA_JVM_OPTION} -cp ${JAR_LIB} ${MAIN_CLASS}  "

echo "${JAVA}  ${JAVA_JVM_OPTION} "

${JAVA} ${JAVA_JVM_OPTION} 
    
      
