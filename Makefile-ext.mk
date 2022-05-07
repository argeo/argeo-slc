include sdk.mk
.PHONY: clean all osgi

all: osgi

A2_CATEGORY = org.argeo.tp

BUNDLES = \
ext/org.argeo.ext.slf4j \
ext/javax.mail.mbox \

clean:
	rm -rf $(BUILD_BASE)

A2_OUTPUT = $(SDK_BUILD_BASE)/a2
A2_BASE = $(A2_OUTPUT)

VPATH = .:ext
DEP_CATEGORIES = org.argeo.tp

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk