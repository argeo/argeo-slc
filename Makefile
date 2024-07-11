include sdk.mk
.PHONY: clean all osgi jni

all: osgi-all
	$(MAKE) -f Makefile-rcp.mk all
	
install: osgi-install

uninstall: osgi-uninstall

#jni:
#	$(MAKE) -C jni

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.api.slc \
org.argeo.slc.runtime \
org.argeo.slc.cms \
org.argeo.rt.cms \
lib/linux/org.argeo.slc.systemd \
swt/org.argeo.cms.e4 \
swt/rap/org.argeo.cms.e4.rap \
swt/rap/org.argeo.tool.rap.cli \
swt/rap/org.argeo.tool.server \

DEP_CATEGORIES = \
crypto/fips/org.argeo.tp.crypto \
log/syslogger/org.argeo.tp \
org.argeo.tp \
org.argeo.tp.httpd \
org.argeo.tp.sys \
osgi/equinox/org.argeo.tp.osgi.framework \
osgi/equinox/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.eclipse \
swt/rap/org.argeo.tp.swt \
swt/rap/org.argeo.tp.swt.workbench \
org.argeo.cms \
swt/org.argeo.cms \
swt/rap/org.argeo.cms \
lib/linux/x86_64/org.argeo.tp.sys \
$(A2_CATEGORY)

NATIVE_PACKAGES= \
org_argeo_api_uuid_libuuid

clean: osgi-clean
#	rm -rf $(BUILD_BASE)
#	$(MAKE) -C jni clean
	$(MAKE) -f Makefile-rcp.mk clean

native-deps-debian:
	sudo apt install uuid-dev

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk