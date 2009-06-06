#!/bin/sh

#
# To be overridden
#
TO=mathieu.baudier@gmail.com
PROFILES=$1

#
# Code
#
BUILD_DIR=`pwd`
HOSTNAME=`hostname -f`
DATE=`date -u`

# BUILD
mvn --fail-at-end deploy -P$PROFILES 2>&1 | tee deploy.log
if [ $? eq 0 ] ; then
	mail -s "Build@$HOSTNAME SUCCESS - $DATE - $BUILD_DIR" $TO < pom.xml
else
	mail -s "Build@$HOSTNAME FAILURE - $DATE - $BUILD_DIR" $TO < deploy.log
fi

# SITE
mvn --fail-at-end site-deploy -P$PROFILES 2>&1 | tee siteGeneration.log
if [ $? eq 0 ] ; then
	echo No need to send email for successful site generation
else
	mail -s "SiteGeneration@$HOSTNAME FAILURE - $DATE - $BUILD_DIR" $TO < siteGeneration.log
fi
