#!/bin/sh
#rm -rf ~/dev/work/slcSite/0.11

BUILD_DIR=`pwd`
HOSTNAME=`hostname -f`
DATE=`date -u`
TO=mathieu.baudier@gmail.com

# BUILD
mvn --fail-at-end deploy 2>&1 | tee deploy.log
if [ $? != "0" ] ; then
	mail -s "Build@$HOSTNAME FAILURE - $DATE - $BUILD_DIR" $TO < deploy.log
else
	mail -s "Build@$HOSTNAME SUCCESS - $DATE - $BUILD_DIR" $TO < deploy.log
fi

# SITE
mvn --fail-at-end site-deploy 2>&1 | tee siteGeneration.log
if [ $? != "0" ] ; then
	mail -s "SiteGeneration@$HOSTNAME FAILURE - $DATE - $BUILD_DIR" $TO < siteGeneration.log
else
	# No need to send email for successful site generation
fi

