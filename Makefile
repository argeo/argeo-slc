include sdk.mk
.PHONY: clean all osgi jni

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.api.slc \
org.argeo.slc.runtime \
org.argeo.slc.cms \
org.argeo.slc.lib.dbus \
swt/org.argeo.cms.e4 \
swt/rap/org.argeo.cms.e4.rap \
org.argeo.slc.mail \
org.argeo.slc.lib.jetty \
org.argeo.slc.jakarta.websocket \
org.argeo.slc.lib.libvirt \

DEP_CATEGORIES = \
crypto/fips/org.argeo.tp.crypto \
log/syslogger/org.argeo.tp \
org.argeo.tp \
org.argeo.tp.httpd \
org.argeo.tp.sys \
osgi/equinox/org.argeo.tp.osgi.framework \
osgi/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.eclipse \
swt/rap/org.argeo.tp.swt \
swt/rap/org.argeo.tp.swt.workbench \
org.argeo.cms \
swt/org.argeo.cms \
swt/rap/org.argeo.cms \
$(A2_CATEGORY)

all: web osgi-all
	$(MAKE) -f Makefile-rcp.mk all
	
install: osgi-install

uninstall: osgi-uninstall

#jni:
#	$(MAKE) -C jni

## WEB
web:
	make -C js all

clean: osgi-clean
	$(MAKE) -f Makefile-rcp.mk clean
	make -C js clean

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk