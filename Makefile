include sdk.mk
.PHONY: clean all osgi jni

all: osgi jni
	$(MAKE) -f Makefile-rcp.mk all
	
install: osgi-install

uninstall: osgi-uninstall

jni:
	$(MAKE) -C jni

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.slc.api \
org.argeo.slc.runtime \
org.argeo.slc.cms \
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
org.argeo.tp.utils \
osgi/api/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.eclipse \
swt/rap/org.argeo.tp.swt \
swt/rap/org.argeo.tp.swt.workbench \
org.argeo.cms \
swt/org.argeo.cms \
swt/rap/org.argeo.cms \
$(A2_CATEGORY)

clean:
	rm -rf $(BUILD_BASE)
	$(MAKE) -C jni clean
	$(MAKE) -f Makefile-rcp.mk clean

native-deps-debian:
	sudo apt install uuid-dev

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk