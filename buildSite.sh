#!/bin/sh
rm -rf ~/dev/work/slcSite/0.11
mvn clean install
mvn site-deploy -Pdeveloper
