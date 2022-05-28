include sdk.mk
.PHONY: clean all osgi

all: osgi

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.slc.api \
org.argeo.slc.factory \
org.argeo.slc.runtime \
cms/org.argeo.slc.cms \

VPATH = .:cms

clean:
	rm -rf $(BUILD_BASE)

A2_OUTPUT = $(SDK_BUILD_BASE)/a2
A2_BASE = $(A2_OUTPUT)

DEP_CATEGORIES = \
org.argeo.tp \
org.argeo.tp.sdk \
org.argeo.tp.apache \
org.argeo.tp.jetty \
org.argeo.tp.eclipse.equinox \
org.argeo.tp.eclipse.rap \
org.argeo.tp.jcr \
org.argeo.tp.formats \
org.argeo.tp.gis \
org.argeo.cms \

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk