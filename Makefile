include sdk.mk
.PHONY: clean all osgi

all: osgi

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.slc.api \
org.argeo.slc.factory \
org.argeo.slc.runtime \
cms/org.argeo.slc.cms \
suite/org.argeo.slc.tool \

VPATH = .:cms:suite

clean:
	rm -rf $(BUILD_BASE)

A2_OUTPUT = $(SDK_BUILD_BASE)/a2
A2_BASE = $(A2_OUTPUT)

DEP_CATEGORIES = \
org.argeo.tp \
org.argeo.tp.sdk \
org.argeo.tp.apache \
org.argeo.tp.jetty \
org.argeo.tp.eclipse \
osgi/api/org.argeo.tp.osgi \
swt/rcp/org.argeo.tp.swt \
lib/linux/x86_64/swt/rcp/org.argeo.tp.swt \
swt/rcp/org.argeo.tp.swt.workbench \
org.argeo.tp.jcr \
org.argeo.tp.formats \
org.argeo.tp.gis \
org.argeo.cms \
org.argeo.cms.eclipse.rcp \

A2_BUNDLES_CLASSPATH = $(subst $(space),$(pathsep),$(strip $(A2_BUNDLES)))

native-image:
	mkdir -p $(A2_OUTPUT)/libexec/$(A2_CATEGORY)
	cd $(A2_OUTPUT)/libexec/$(A2_CATEGORY) && /opt/graalvm-ce/bin/native-image \
		-cp $(A2_CLASSPATH):$(A2_BUNDLES_CLASSPATH) org.argeo.slc.tool.Main \
		--enable-url-protocols=http,https -H:AdditionalSecurityProviders=sun.security.jgss.SunProvider \
		--initialize-at-build-time=org.argeo.init.logging.ThinLogging,org.slf4j.LoggerFactory \
		--no-fallback 
 

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk