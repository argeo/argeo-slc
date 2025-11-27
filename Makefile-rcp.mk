include sdk.mk
.PHONY: clean all osgi

all: osgi

A2_CATEGORY = org.argeo.slc

BUNDLES = \
swt/rcp/org.argeo.cms.e4.rcp \
swt/rcp/org.argeo.cms.lib.worldwind \

DEP_CATEGORIES = \
org.argeo.tp \
org.argeo.tp.desktop \
lib/org.argeo.tp.desktop \
osgi/equinox/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.eclipse \
swt/rcp/org.argeo.tp.swt \
lib/x86_64-linux-gnu/swt/rcp/org.argeo.tp.swt \
swt/rcp/org.argeo.tp.swt.workbench \
org.argeo.cms \
swt/org.argeo.cms \
swt/rcp/org.argeo.cms \
$(A2_CATEGORY) \
swt/$(A2_CATEGORY) \
swt/rcp/$(A2_CATEGORY) \

clean:
	rm -rf $(BUILD_BASE)

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk