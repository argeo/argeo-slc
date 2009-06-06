#!/bin/sh
#rm -rf ~/dev/work/slcSite/0.11

BUILD_DIR=`pwd`
HOSTNAME=`hostname -f`
DATE=`date -u`
TO=mathieu.baudier@gmail.com

# BUILD
mvn --fail-at-end deploy
if [ $? != "0" ] ; then
	mail -s "Build@$HOSTNAME FAILURE - $DATE - $BUILD_DIR" $TO < pom.xml
else
	mail -s "Build@$HOSTNAME SUCCESS - $DATE - $BUILD_DIR" $TO < pom.xml
fi

# SITE
mvn --fail-at-end site-deploy
if [ $? != "0" ] ; then
	mail -s "SiteGeneration@$HOSTNAME FAILURE - $DATE - $BUILD_DIR" $TO < pom.xml
else
	mail -s "SiteGeneration@$HOSTNAME SUCCESS - $DATE - $BUILD_DIR" $TO < pom.xml
fi

