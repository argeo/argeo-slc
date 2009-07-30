#!/bin/sh
INSTANCE=$1

# Defaults
if [ -z "$INSTANCE" ]; then
	INSTANCE=agent
fi

echo "Using configuration: $INSTANCE" 

WORK_DIR=work/$INSTANCE
if [ -z "$JAVA_OPTS" ]; then
	JAVA_OPTS=-Xmx256m
fi

if [ -d "$WORK_DIR" ]; then
java $JAVA_OPTS -jar lib/org.eclipse.osgi-${version.equinox}.jar \
	-clean -console \
	-configuration $WORK_DIR/conf \
	-data $WORK_DIR/data
else
 echo "$WORK_DIR does not exist. Please specify a proper configuration name."
fi
