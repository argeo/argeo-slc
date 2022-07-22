include sdk.mk
.PHONY: clean all osgi

all: osgi

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.slc.api \
org.argeo.slc.factory \
org.argeo.slc.runtime \
cms/org.argeo.slc.cms \
swt/rap/org.argeo.tool.server \

VPATH = .:cms:swt/rap

clean:
	rm -rf $(BUILD_BASE)

A2_OUTPUT = $(SDK_BUILD_BASE)/a2
A2_BASE = $(A2_OUTPUT)

DEP_CATEGORIES = \
org.argeo.tp \
org.argeo.tp.sdk \
org.argeo.tp.apache \
org.argeo.tp.jetty \
osgi/api/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.eclipse \
swt/rap/org.argeo.tp.swt \
swt/rap/org.argeo.tp.swt.workbench \
org.argeo.tp.jcr \
org.argeo.tp.formats \
org.argeo.tp.gis \
org.argeo.cms \
swt/rap/org.argeo.cms \

GRAALVM_HOME = /opt/graalvm-ce
A2_BUNDLES_CLASSPATH = $(subst $(space),$(pathsep),$(strip $(A2_BUNDLES)))

graalvm-custom:
	$(GRAALVM_HOME)/bin/java -jar $(ECJ_JAR) @$(SDK_SRC_BASE)/sdk/argeo-build/ecj.args -cp $(A2_CLASSPATH) \
		graalvm/org.argeo.slc.graalvm/src[-d $(SDK_BUILD_BASE)/$(A2_CATEGORY)/graalvm/bin]

tool-server: osgi graalvm-custom
	mkdir -p $(A2_OUTPUT)/libexec/$(A2_CATEGORY)
	cd $(A2_OUTPUT)/libexec/$(A2_CATEGORY) && $(GRAALVM_HOME)/bin/native-image \
		-cp $(A2_CLASSPATH):$(A2_BUNDLES_CLASSPATH):$(SDK_BUILD_BASE)/$(A2_CATEGORY)/graalvm/bin \
		--features=org.argeo.slc.graalvm.feature.ArgeoToolFeature \
		--enable-url-protocols=http,https \
		-H:AdditionalSecurityProviders=sun.security.jgss.SunProvider,org.bouncycastle.jce.provider.BouncyCastleProvider,net.i2p.crypto.eddsa.EdDSASecurityProvider \
		--initialize-at-build-time=org.argeo.init.logging.ThinLogging,org.slf4j.LoggerFactory \
		--no-fallback \
		 org.argeo.tool.server.ArgeoServer \
		 argeo


include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk