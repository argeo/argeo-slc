include sdk.mk
.PHONY: clean all osgi

all: osgi

A2_CATEGORY = org.argeo.slc

BUNDLES = \
swt/rap/org.argeo.cms.e4.rap \

DEP_CATEGORIES = \
org.argeo.tp \
osgi/equinox/org.argeo.tp.osgi.framework \
osgi/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.eclipse \
org.argeo.cms \
swt/org.argeo.cms \
swt/rap/org.argeo.tp.swt \
swt/rap/org.argeo.tp.swt.workbench \
swt/rap/org.argeo.cms \

clean:
	rm -rf $(BUILD_BASE)

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk