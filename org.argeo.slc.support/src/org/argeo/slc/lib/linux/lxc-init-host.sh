#!/bin/sh
CHROOT=$1
echo Init LXC container $CHROOT

mkdir $CHROOT/etc/yum.repos.d/ -p  
cat /etc/yum.repos.d/CentOS-Base.repo |sed s/'$releasever'/6/g > $CHROOT/etc/yum.repos.d/CentOS-Base.repo
yum groupinstall core --installroot=$CHROOT --nogpgcheck -y
yum install plymouth libselinux-python --installroot=$CHROOT --nogpgcheck -y