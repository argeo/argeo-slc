#!/bin/sh
INSTANCE=$1

ROOT_DIR=`pwd`
LIB_DIR=$ROOT_DIR/lib

# Defaults
if [ -z "$INSTANCE" ]; then
	INSTANCE=agent
fi

echo "Using configuration: $INSTANCE" 

WORK_DIR=$ROOT_DIR/work/$INSTANCE
if [ -z "$JAVA_OPTS" ]; then
	JAVA_OPTS=-Xmx256m
fi

if [ -d "$WORK_DIR" ]; then
cd $WORK_DIR
java $JAVA_OPTS -jar $LIB_DIR/org.eclipse.osgi-${version.equinox}.jar \
	-clean -console \
	-configuration $WORK_DIR/conf \
	-data $WORK_DIR/data
else
 echo "$WORK_DIR does not exist. Please specify a proper configuration name."
fi
