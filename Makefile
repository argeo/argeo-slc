include sdk.mk
.PHONY: clean all osgi jni

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.api.slc \
org.argeo.api.js \
org.argeo.slc.runtime \
org.argeo.slc.cms \
org.argeo.slc.lib.dbus \
org.argeo.slc.mail \
org.argeo.slc.ux \
org.argeo.slc.lib.jetty \
org.argeo.slc.jakarta.websocket \
org.argeo.slc.lib.libvirt \
swt/org.argeo.slc.geo.swt \
swt/org.argeo.cms.e4 \
swt/rcp/org.argeo.cms.e4.rcp \
swt/rcp/org.argeo.cms.lib.worldwind \

DEP_CATEGORIES = \
crypto/fips/org.argeo.tp.crypto \
log/syslogger/org.argeo.tp \
org.argeo.tp \
org.argeo.tp.httpd \
org.argeo.tp.sys \
lib/org.argeo.tp.sys \
osgi/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.osgi.framework \
osgi/equinox/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.eclipse \
org.argeo.tp.desktop \
lib/local/swt/rcp/org.argeo.tp.swt \
swt/rcp/org.argeo.tp.swt \
swt/rcp/org.argeo.tp.swt.workbench \
\
org.argeo.cms \
swt/org.argeo.cms \
$(A2_CATEGORY) \
swt/$(A2_CATEGORY) \
swt/rcp/org.argeo.cms \
swt/rcp/$(A2_CATEGORY) \

all: osgi-all
	$(MAKE) -f Makefile-rap.mk all
	$(MAKE) -C js all
	
install: osgi-install

uninstall: osgi-uninstall

#jni:
#	$(MAKE) -C jni

clean: osgi-clean
	$(MAKE) -f Makefile-rap.mk clean
	$(MAKE) -C js clean

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk