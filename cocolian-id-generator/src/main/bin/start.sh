 #!/bin/bash
 #
 # Author: Shamphone Lee

 PRODUCTION_ROOT_DIR=$(cd `dirname $0`; pwd)
 PRODUCTION_ROOT_DIR="$(dirname ${PRODUCTION_ROOT_DIR})"
 echo "running in ${PRODUCTION_ROOT_DIR}"
 JAR_LIB="${PRODUCTION_ROOT_DIR}/lib/*"
 MAIN_CLASS="org.cocolian.data.server.DataServer"

 SERVICE_JAR=$PRODUCTION_ROOT_DIR/${project.build.finalName}.jar

# JAVA_JVM_OPTION="-Xmx25g -Xms25g -XX:NewSize=15g -XX:MaxNewSize=15g -XX:SurvivorRatio=17 -XX:PermSize=256m -XX:MaxPermSize=256m"

JAVA_JVM_OPTION="-Xmx1g -Xms1g"
JAVA_JVM_OPTION="${JAVA_JVM_OPTION} -Duser.timezone=\"GMT+8\" -Dlog_path=\"${PRODUCTION_ROOT_DIR}/logs\""
JAVA_JVM_OPTION="${JAVA_JVM_OPTION} -classpath \"${JAR_LIB}\" ${MAIN_CLASS}  "

echo "java  ${JAVA_JVM_OPTION}"
java  ${JAVA_JVM_OPTION}
    
      
